package com.kynetics.updatefactory.dcu.workers

import android.content.Context
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kynetics.uf.android.update.dcu.api.DCUNotification
import com.kynetics.uf.android.update.dcu.api.DCUServiceNotifier
import com.kynetics.updatefactory.dcu.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WirelessWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.i(TAG, "Changing access point")
        return if(connectToAndroidWifi()){
            DCUServiceNotifier.notify(applicationContext, DCUNotification(
                listOf("Changing access point successfully changed"), immediately = true, error = false, BuildConfig.APPLICATION_ID
            )
            )
            Result.success()
        } else {
            Log.i(TAG, "Error on changing access point")
            DCUServiceNotifier.notify(applicationContext, DCUNotification(
                listOf("Error on changing access point"), immediately = true, error = false, BuildConfig.APPLICATION_ID
            )
            )
            Result.retry()
        }

    }


    @Suppress("DEPRECATION")
    //Not deprecated for system apps.
    private fun connectToAndroidWifi(networkSSID:String = "<>", networkPass: String = "<>"):Boolean{
        return runBlocking {
            DCUServiceNotifier.notify(applicationContext, DCUNotification(
                listOf("Changing access point to $networkSSID"), immediately = true, error = false, BuildConfig.APPLICATION_ID
            )
            )
            val wifiConfig = WifiConfiguration()
                .apply {
                    SSID = "\"$networkSSID\""
                    preSharedKey = "\"$networkPass\""
                    hiddenSSID = true;
                    status = WifiConfiguration.Status.ENABLED;

                    allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                    allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                    allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                    allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                }
            with(applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager){
                if(connectionInfo.ssid == "\"$networkSSID\""){
                    true
                } else {
                    if (!isWifiEnabled) {
                        Log.i(TAG, "Enabling wifi")
                        setWifiEnabled(true).also { Log.i(TAG, "Wifi enabled? $it") }
                    }
                    val netId: Int = addNetwork(wifiConfig).also { Log.i(TAG, "netId - $it") }
                    if (
                        netId != -1 && disconnect().also { Log.i(TAG, "disconnected - $it") } &&
                        enableNetwork(netId, true).also { Log.i(TAG, "enableNetwork - $it") } &&
                        reconnect().also { Log.i(TAG, "reconnect - $it") }
                    ) {
                        (1..10).firstOrNull {
                            delay(1000)
                            Log.i(
                                TAG,
                                "Waiting for ${SupplicantState.COMPLETED} state, current state is ${connectionInfo.supplicantState} ${connectionInfo.ssid}"
                            )
                            connectionInfo.supplicantState == SupplicantState.COMPLETED && connectionInfo.ssid == "\"$networkSSID\""
                        } != null
                    } else {
                        false
                    }
                }
            }
        }

    }


    companion object{
        private val TAG:String = WirelessWorker::class.java.simpleName
    }
}
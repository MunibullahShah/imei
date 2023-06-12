package com.example.imei;

import static android.Manifest.permission.READ_PHONE_STATE;

import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

public class IMEI {


    public static String getIMEI(Context context) {
        String imei = "";
        String[] imeiGuesses = {
                "getImei",
                "getDeviceId",
                "getDeviceIdDs",
                "getDeviceIdGemini",
                "getSimSerialNumberGemini"
        };
        //Check IMEI's from SIM Slot 1
        for (String imeiGuess : imeiGuesses) {
            try {
                if (imei.isEmpty()) {
                    imei = getDeviceIdBySlot(context, imeiGuess, 0);
                    System.out.println("IMEI SIM 1 Found With Method: " + imeiGuess);
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println(imeiGuess + " Method did not worked.");
            }
        }
        //Check IMEI's from SIM Slot 2
        if (imei.isEmpty()) {
            for (String imeiGuess : imeiGuesses) {
                try {
                    if (imei.isEmpty()) {
                        imei = getDeviceIdBySlot(context, imeiGuess, 1);
                        System.out.println("IMEI SIM 1 Found With Method: " + imeiGuess);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(imeiGuess + " Method did not worked.");
                }
            }
        }
        if (imei.isEmpty()) {
            imei = "000000000";
        }
        return imei;
    }

    public static String getIMSI(Context context) {
        String imsi = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && ContextCompat.checkSelfPermission(ContextUtil.getAppContext(), READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionInfo activeSubscriptionInfo = getSimSlotInfo(context);
            if (activeSubscriptionInfo != null) {
                imsi = activeSubscriptionInfo.getIccId();
            }
        } else {
            String[] imsiGuesses = {
                    "getSubscriberId",
                    "getSubscriberIdGemini",
                    "getSimSerialNumber"
            };
            //Check IMSI's from SIM Slot 1
            for (String imsiGuess : imsiGuesses) {
                try {
                    if (imsi.isEmpty()) {
                        imsi = getDeviceIdBySlot(context, imsiGuess, 0);
                       System.out.println("IMSI SIM 1 Found With Method: " + imsiGuess);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println((imsiGuess + " Method did not worked."));
                }
            }
            //Check IMSI's from SIM Slot 2
            if (imsi.isEmpty()) {
                for (String imsiGuess : imsiGuesses) {
                    try {
                        if (imsi.isEmpty()) {
                            imsi = getDeviceIdBySlot(context, imsiGuess, 1);
                            System.out.println(("IMSI SIM 1 Found With Method: " + imsiGuess));
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println((imsiGuess + " Method did not worked."));
                    }
                }
            }
        }
        if (imsi.isEmpty()) {
            imsi = "DEFAULT_IMSI_IMEI";
        }
        return imsi;
    }


    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imsi_or_imei = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (telephony != null) {
                Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

                Object[] obParameter = new Object[1];
                obParameter[0] = slotID;
                Object ob_phone = getSimID.invoke(telephony, obParameter);

                if (ob_phone != null) {
                    imsi_or_imei = ob_phone.toString();
                }
            }
        } catch (Exception e) {
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imsi_or_imei;
    }

    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}

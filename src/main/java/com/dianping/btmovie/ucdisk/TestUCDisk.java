package com.dianping.btmovie.ucdisk;

import com.dianping.btmovie.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by oceanzhang on 16/1/12.
 */
public class TestUCDisk {
    static UCDiskConnectService httpService = UCDiskConnectService.getService();
    public static void main(String ... args){
        try {
            HashMap<String,String> headers = new HashMap<String, String>();
            headers.put("Cookie", "_uab_collina=145266942204591908288002; _umdata=535523100CBE37C35FEC4931D5F9CAF3413EC013C2F8E7FE310A8288CB412692EFEB69036032023F525EB6438264E4A71035F7D44EEB01A60D2F744D99A6ACB97B37131448EB2C04; _UP_RI_=cd1458c3-d046-4e35-b682-45e37849709d; _UP_6D1_64_=069; _UP_E8A_7D_=837051151`z02110917``1452670459; _UP_E37_B7_=sg1a4e25e3a9074645107f5a0447fdf84a7; _UP_F7E_8D_=\"d6l+og/Fn2O+nKPyaFvmFkPwKLOVbxJPcg0RzQPI6KkAb4403nUydBFHs+Q/pshPAMtlYPMuyO4GjNiFpCOHD+7rSXFm5RjbW1/mvBLvMwKTOk2wEvbokFCfwAd1vIZUTkguXFIpylhYIGPd1Sag/uE3CCjybdDCPU2ESSGfc7c=\"; _UP_D_=mobile; _UP_BT_=html5; _UP_L_=zh; _UP_30C_6A_=wx641263c21b4a589fe6cd8950329f25");
            Response resp = httpService.execute("http://api.open.uc.cn/cas/login?client_id=37&uc_param_str=nieisivefrpfbilaprligiwiutst&client_id=37&browsertype=html5", headers);
            System.out.println(resp.getData());

            InputStream is=TestUCDisk.class.getResourceAsStream("/cookie");
            System.out.println(IOUtils.readStreamUTF8(is));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

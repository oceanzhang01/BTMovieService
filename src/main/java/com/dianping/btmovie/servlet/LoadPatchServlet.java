package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.bmob.BmobHelper;
import org.apache.http.util.TextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oceanzhang on 15/12/11.
 */
@WebServlet(name = "LoadPatchServlet",urlPatterns = "/loadPatch")
public class LoadPatchServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        String codeStr = request.getParameter("code");
        String versionCodeStr = request.getParameter("versionCode");
        PrintWriter pw = response.getWriter();
        if(TextUtils.isEmpty(codeStr) || TextUtils.isEmpty(versionCodeStr)){
            jsonRerutn(pw,-1,"code cannot be null.");
            return;
        }
        int code = 0;
        int versionCode = 0;
        try {
             code = Integer.parseInt(codeStr);
            versionCode = Integer.parseInt(versionCodeStr);
        }catch (NumberFormatException e){
            jsonRerutn(pw,-1,"code muse be number format.");
            return;
        }
        JSONObject patch = BmobHelper.getPatch();
        if(patch != null){
            if(code < patch.getInteger("code") && versionCode < patch.getInteger("versionCode")){ //有补丁
                //
                Map<String,String> map = new HashMap<String, String>(3);
                map.put("code", patch.getIntValue("code") + "");
                map.put("versionCode",patch.getInteger("versionCode")+"");
                String url = "http://file.bmob.cn/"+patch.getJSONObject("patchFile").getString("url");
                map.put("path",url);
                jsonRerutn(pw, 0, "please download this patch and load.", map);
                return;
            }
        }
        jsonRerutn(pw,-1,"cannot find any patch to load.");
    }

}

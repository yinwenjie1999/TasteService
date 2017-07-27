package com.firstTaste.crm.services.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.firstTaste.crm.services.iface.ShortMessageService;

@Service("ShortMessageServiceImpl")
public class ShortMessageServiceImpl implements ShortMessageService {

  private static final String URL = "http://api.jisuapi.com/sms/send";

  private static final String CONTENT = "您当期花品已配送，中通货运单号为:%s。微信关注服务号 hddflowers 享受更多优惠【花点点】";

  @Value("${shortMessage.appkey}")
  private String appkey;

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.ShortMessageService#sendMessage(java.lang.String)
   */
  @Override
  public Integer sendMessage(String logisticsNo, String mobile) throws IllegalArgumentException {
    String contentFormat = "";
    try {
      contentFormat = URLEncoder.encode(String.format(CONTENT, logisticsNo), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
    String url = URL + "?mobile=" + mobile + "&content=" + contentFormat + "&appkey=" + appkey;

    // 发送，并等待返回
    HttpClient httpClient = new HttpClient();
    GetMethod get = new GetMethod(url);
    int state = 0;
    String result = "";
    try {
      state = httpClient.executeMethod(get);
      if (state != 200) {
        throw new IllegalArgumentException("短信服务状态不正确!");
      }
      result = get.getResponseBodyAsString();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }

    // 确定返回信息是否正确
    JSONObject json = JSONObject.parseObject(result);
    if (json.getInteger("status") != 0) {
      String errorMsg = json.getString("msg");
      throw new IllegalArgumentException(errorMsg);
    }
    return 0;
  }
}

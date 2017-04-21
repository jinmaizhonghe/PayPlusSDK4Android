package com.mobpex.sdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import g3.yeepay.com.qianmaisdk.aliPay.Alipay;
import g3.yeepay.com.qianmaisdk.weixinPay.WXpay;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RadioGroup payEnvRadioGroup,payModeRadioGroup;
    RadioButton qaBtn,productBtn,aliPayBtn,wechatPayBtn;
    Button confirmBtn;
    EditText payAmountEdt;
    String payEnv,payMode,payAmount;
    String baseUrl="http://172.17.102.190:8014";
    String payCharge="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        payEnvRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==qaBtn.getId()){
                    payEnv="qa";
                }else if(checkedId==productBtn.getId()){
                    payEnv="product";
                }
            }
        });

        payModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==aliPayBtn.getId()){
                    payMode="ALIPAYSDK";
                }else if(checkedId==wechatPayBtn.getId()){
                    payMode="WECHATSDK";
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getOrderInfo();
//                    }
//                }).start();
//                if(payMode.equals("ALIPAYSDK")){
//                    doAliPay(payCharge);
//                }else if(payMode.equals("WECHATSDK")){
//                    doWXPay(payCharge);
//                }
                ExecutorService executor = Executors.newSingleThreadExecutor();
                FutureTask<String> future =new FutureTask<String>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String result=getOrderInfo();
                        return result;
                    }
                });
                executor.execute(future);
                try {
                    String payCharge=future.get();
                    if(payMode.equals("ALIPAYSDK")){
                        doAliPay(payCharge);
                    }else if(payMode.equals("WECHATSDK")){
                        doWXPay(payCharge);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView(){
        payEnvRadioGroup=(RadioGroup) findViewById(R.id.payEnv);
        payModeRadioGroup=(RadioGroup) findViewById(R.id.payMode);
        qaBtn=(RadioButton) findViewById(R.id.qaEnv);
        productBtn=(RadioButton) findViewById(R.id.productEnv);
        aliPayBtn=(RadioButton) findViewById(R.id.aliPay);
        wechatPayBtn=(RadioButton) findViewById(R.id.wechatPay);
        confirmBtn=(Button) findViewById(R.id.confirmBtn);
        payAmountEdt=(EditText) findViewById(R.id.payAmount);
    }

    private String getOrderInfo(){
        StringBuilder orderUrl=new StringBuilder();
        payAmount=payAmountEdt.getText().toString();
        String orderInfo="";
        orderUrl.append(baseUrl).append("/cashier-test/test/appPayPreparePay?").append("env=").append(payEnv).append("&orderAmount=")
                .append(payAmount).append("&payTool=").append(payMode);
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(orderUrl.toString()).build();
        try {
            Response response=client.newCall(request).execute();
            orderInfo= response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(orderInfo!=null){
            try {
                JSONObject jsonObject=new JSONObject(orderInfo);
//                String payChannel=jsonObject.getString("payChannal");
                payCharge=jsonObject.getString("payCharge");
//                String timestamp=payCharge.getString("timestamp");
//                String sign=payCharge.getString("sign");
//                String partnerid=payCharge.getString("partnerid");
//                String noncestr=payCharge.getString("noncestr");
//                String prepayid=payCharge.getString("prepayid");
//                String packages=payCharge.getString("package");
//                String appid=payCharge.getString("appid");
//                if(payChannel.equals("PayPlusWxpay")){
//                    doWXPay(payCharge);
//                }else if(payChannel.equals("PayPlusAlipay")){
//                    doAliPay(payCharge);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return payCharge;
    }

    private void doWXPay(String pay_params){
        String wx_appid="wx95f2b27daf603387";
        WXpay.init(getApplicationContext(),wx_appid);
        WXpay.getInstance().doPay(pay_params, new WXpay.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(),"支付成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case WXpay.NO_OR_LOW_WX:
                        Toast.makeText(getApplication(), "未安装微信或微信版本过低", Toast.LENGTH_SHORT).show();
                        break;

                    case WXpay.ERROR_PAY_PARAM:
                        Toast.makeText(getApplication(), "参数错误", Toast.LENGTH_SHORT).show();
                        break;

                    case WXpay.ERROR_PAY:
                        Toast.makeText(getApplication(), "支付失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doAliPay(String pay_params){
        new Alipay(this, pay_params, new Alipay.AlipayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDealing() {
                Toast.makeText(getApplication(), "支付处理中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case Alipay.ERROR_RESULT:
                        Toast.makeText(getApplication(), "支付失败:支付结果解析错误", Toast.LENGTH_SHORT).show();
                        break;

                    case Alipay.ERROR_NETWORK:
                        Toast.makeText(getApplication(), "支付失败:网络连接错误", Toast.LENGTH_SHORT).show();
                        break;

                    case Alipay.ERROR_PAY:
                        Toast.makeText(getApplication(), "支付错误:支付码支付失败", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(getApplication(), "支付错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        }).doPay();
    }
}

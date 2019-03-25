package com.example.sivakumaran.payment_gateway;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sivakumaran.payment_gateway.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import static com.paypal.android.sdk.cu.s;

public class MainActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 7171 ;
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    Button btnPayNow;
    TextView tvp,tvh,tvf,total;
    String amount="";

    @Override
    protected void onDestroy(){
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start payment service
        Intent intent=new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);




        btnPayNow=(Button)findViewById(R.id.btnPayNow);
        tvf=findViewById(R.id.tvfcost);
        tvp=findViewById(R.id.tvpcost);
        tvh=findViewById(R.id.tvhcost);
        total=findViewById(R.id.tcost);
        total.setText("6400");


        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();


            }
        });



    }
    private void processPayment(){
        amount=Double.toString(Double.parseDouble(total.getText().toString())/69);

        PayPalPayment payPalPayment =new PayPalPayment(new BigDecimal(String.valueOf(amount)),"USD"," Pay for trip",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent=new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);



    }

    protected void onActivtyResult(int requestCode,int resultCode,Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class).putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else if(resultCode== Activity.RESULT_CANCELED) {
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();

            }


        }
        else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this,"invalid",Toast.LENGTH_SHORT).show();


    }

}

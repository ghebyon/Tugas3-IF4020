package com.fsck.k9.message;



import com.fsck.k9.message.apicrypto.RetrofitClient;
import com.fsck.k9.message.apicrypto.encrypt;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RunCrypto {

    public String encryptResult = "";
    private CountDownLatch responseReceived = new CountDownLatch(1);

    public void getEncrypt(String message, String key){
        Call<encrypt> encryptCall = RetrofitClient.INSTANCE.getInstace().getEncrypt(message, key);
        encryptCall.enqueue(new Callback<encrypt>() {
            @Override
            public void onResponse(Call<encrypt> call, Response<encrypt> response) {
                if(response.isSuccessful()){
                    encrypt encrypt = response.body();
                    String message = encrypt.getEncrypt();
                    encryptResult = message;
                    responseReceived.countDown();
                }

            }

            @Override
            public void onFailure(Call<encrypt> call, Throwable t) {
                //Failure
                responseReceived.countDown();
            }
        });
    }

    public String getEncryptResult() throws InterruptedException{
        responseReceived.await();
        return this.encryptResult;
    }
}

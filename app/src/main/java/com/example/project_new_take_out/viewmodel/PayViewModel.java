package com.example.project_new_take_out.viewmodel;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.model.PaymentInfo;

/**
 * 支付页 ViewModel
 * 管理支付信息、支付方式选择、15 分钟支付倒计时
 */
public class PayViewModel extends AndroidViewModel {

    private final MutableLiveData<PaymentInfo> paymentInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> payMethodLiveData = new MutableLiveData<>("wechat");
    private final MutableLiveData<String> countdownLiveData = new MutableLiveData<>("15:00");
    private final MutableLiveData<Long> remainingSecondsLiveData = new MutableLiveData<>(900L);
    private final MutableLiveData<Boolean> isExpiredLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> paySuccessLiveData = new MutableLiveData<>(false);

    private CountDownTimer countDownTimer;
    private Order order;

    public PayViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化支付信息
     */
    public void initPayment(Order order) {
        this.order = order;
        PaymentInfo paymentInfo = new PaymentInfo(order.getOrderId(), order.getActualAmount());
        paymentInfoLiveData.setValue(paymentInfo);
        startCountdown(paymentInfo);
    }

    /**
     * 开始 15 分钟支付倒计时
     */
    private void startCountdown(PaymentInfo paymentInfo) {
        long remainingMs = paymentInfo.getExpireTime() - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(remainingMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSec = millisUntilFinished / 1000;
                long min = totalSec / 60;
                long sec = totalSec % 60;
                countdownLiveData.setValue(String.format("%02d:%02d", min, sec));
                remainingSecondsLiveData.setValue(totalSec);
            }

            @Override
            public void onFinish() {
                isExpiredLiveData.setValue(true);
                countdownLiveData.setValue("00:00");
                remainingSecondsLiveData.setValue(0L);
            }
        };
        countDownTimer.start();
    }

    /**
     * 选择支付方式
     */
    public void selectPayMethod(String method) {
        payMethodLiveData.setValue(method);
    }

    /**
     * 模拟支付（延时 2 秒后返回成功）
     */
    public void confirmPay() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            paySuccessLiveData.setValue(true);
        }, 2000);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // ========== Getter ==========
    public LiveData<PaymentInfo> getPaymentInfoLiveData() { return paymentInfoLiveData; }
    public LiveData<String> getPayMethodLiveData() { return payMethodLiveData; }
    public LiveData<String> getCountdownLiveData() { return countdownLiveData; }
    public LiveData<Long> getRemainingSecondsLiveData() { return remainingSecondsLiveData; }
    public LiveData<Boolean> getIsExpiredLiveData() { return isExpiredLiveData; }
    public LiveData<Boolean> getPaySuccessLiveData() { return paySuccessLiveData; }
    public Order getOrder() { return order; }
}

package com.example.project_new_take_out.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息中心页面
 */
public class MessageCenterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.recycler_messages);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("系统通知", "欢迎使用网上订餐App！新用户下单立减10元。", "今天 09:30"));
        messages.add(new Message("订单更新", "您的订单DD20260701001已支付成功，商家正在备餐中。", "今天 10:15"));
        messages.add(new Message("优惠提醒", "您有一张满50减15优惠券即将到期，快去使用吧！", "昨天 14:20"));
        messages.add(new Message("活动通知", "周末特惠活动火热进行中，精选美食5折起！", "昨天 10:00"));
        messages.add(new Message("配送通知", "您的订单DD20260630002已送达，请确认收货。", "6月30日"));
        messages.add(new Message("系统通知", "欢迎使用网上订餐！新用户首单享专属优惠。", "6月28日"));

        recycler.setAdapter(new MessageAdapter(messages));
    }

    static class Message {
        String title, content, time;
        Message(String t, String c, String tm) { title = t; content = c; time = tm; }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
        private final List<Message> list;
        MessageAdapter(List<Message> l) { this.list = l; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_message, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Message m = list.get(pos);
            h.tvTitle.setText(m.title);
            h.tvContent.setText(m.content);
            h.tvTime.setText(m.time);
            h.itemView.setOnClickListener(v ->
                    Toast.makeText(MessageCenterActivity.this, m.title + "\n" + m.content, Toast.LENGTH_LONG).show());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvContent, tvTime;
            VH(@NonNull View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_msg_title);
                tvContent = v.findViewById(R.id.tv_msg_content);
                tvTime = v.findViewById(R.id.tv_msg_time);
            }
        }
    }
}

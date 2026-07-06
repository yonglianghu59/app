package com.example.project_new_take_out.ui.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 客服页面（模拟在线聊天）
 * 包含快捷问题入口 + 聊天界面 + 自动回复
 */
public class CustomerServiceActivity extends BaseActivity {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageView ivSend;
    private LinearLayout layoutQuickBtns;

    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());

    // 快捷问题 & 预设回复
    private static final String[][] QUICK_QA = {
            {"如何下单？", "您好！下单流程：\n1. 在首页选择餐厅\n2. 浏览菜单并选择菜品\n3. 点击"+"加入购物车\n4. 确认订单并支付\n\n如有疑问，随时联系我哦~ 😊"},
            {"配送要多久？", "一般配送时间为 20-50 分钟⏱️\n具体取决于商家出餐速度和配送距离。您可以在店铺详情页查看预计送达时间。"},
            {"如何取消订单？", "在「我的订单」中找到对应订单 → 点击进入详情 → 选择取消。\n⚠️ 注意：商家已接单后取消可能产生费用。"},
            {"如何退款？", "在「我的订单」→ 找到对应订单 → 点击「申请退款」→ 选择退款原因并提交。\n退款将在 1-3 个工作日内处理，请耐心等待。"},
            {"优惠券怎么用？", "下单时在确认订单页面选择可用优惠券即可自动抵扣 💰\n注意：每张优惠券有使用条件和有效期哦~"},
            {"联系人工客服", "正在为您转接人工客服...\n\n💬 人工客服工作时间：9:00-21:00\n📞 客服热线：400-888-8888\n\n当前为模拟客服，如有紧急问题请拨打电话。"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        recyclerChat = findViewById(R.id.recycler_chat);
        etMessage = findViewById(R.id.et_chat_input);
        ivSend = findViewById(R.id.iv_send);
        layoutQuickBtns = findViewById(R.id.layout_quick_btns);

        // 设置 RecyclerView
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter();
        recyclerChat.setAdapter(adapter);

        // 生成快捷问题按钮
        buildQuickButtons();

        // 欢迎消息
        addBotMessage("您好！👋 欢迎联系网上订餐客服中心。\n您可以点击下方快捷问题，或直接输入您的问题，我会尽快为您解答。");

        // 发送按钮
        ivSend.setOnClickListener(v -> sendMessage());
    }

    private void buildQuickButtons() {
        layoutQuickBtns.removeAllViews();
        for (String[] qa : QUICK_QA) {
            TextView btn = new TextView(this);
            btn.setText(qa[0]);
            btn.setTextSize(12);
            btn.setTextColor(getColor(R.color.colorPrimary));
            btn.setBackgroundResource(R.drawable.bg_search_bar);
            btn.setPadding(dp2px(10), dp2px(6), dp2px(10), dp2px(6));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, dp2px(8), dp2px(6));
            btn.setLayoutParams(params);
            btn.setOnClickListener(v -> {
                addUserMessage(qa[0]);
                scrollToBottom();
                // 模拟客服思考
                handler.postDelayed(() -> {
                    addBotMessage(qa[1]);
                    scrollToBottom();
                }, 800);
            });
            layoutQuickBtns.addView(btn);
        }
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        addUserMessage(text);
        etMessage.setText("");

        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

        scrollToBottom();

        // 智能匹配回复
        handler.postDelayed(() -> {
            String reply = findBestReply(text);
            addBotMessage(reply);
            scrollToBottom();
        }, 1000);
    }

    /**
     * 简单关键词匹配回复
     */
    private String findBestReply(String input) {
        String lower = input.toLowerCase();

        // 关键词匹配
        if (lower.contains("下单") || lower.contains("怎么买") || lower.contains("订餐")) {
            return QUICK_QA[0][1];
        }
        if (lower.contains("配送") || lower.contains("多久") || lower.contains("时间") || lower.contains("送")) {
            return QUICK_QA[1][1];
        }
        if (lower.contains("取消") || lower.contains("退单")) {
            return QUICK_QA[2][1];
        }
        if (lower.contains("退款") || lower.contains("退钱") || lower.contains("退")) {
            return QUICK_QA[3][1];
        }
        if (lower.contains("优惠券") || lower.contains("折扣") || lower.contains("券") || lower.contains("红包")) {
            return QUICK_QA[4][1];
        }
        if (lower.contains("人工") || lower.contains("转人工") || lower.contains("电话") || lower.contains("客服")) {
            return QUICK_QA[5][1];
        }
        if (lower.contains("你好") || lower.contains("hi") || lower.contains("hello") || lower.contains("在吗")) {
            return "您好！有什么可以帮您的吗？😊\n您可以描述遇到的问题，或点击下方的快捷问题快速获取帮助。";
        }
        if (lower.contains("谢谢") || lower.contains("感谢") || lower.contains("thank")) {
            return "不客气！很高兴能帮到您 😊\n如有其他问题，随时找我哦~";
        }

        // 默认回复
        return "感谢您的反馈！我已收到您的问题：「" + input + "」\n\n💡 建议您点击下方的快捷问题，或拨打客服热线 400-888-8888 获取更及时的帮助。";
    }

    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, true, System.currentTimeMillis()));
        adapter.notifyItemInserted(messages.size() - 1);
    }

    private void addBotMessage(String text) {
        messages.add(new ChatMessage(text, false, System.currentTimeMillis()));
        adapter.notifyItemInserted(messages.size() - 1);
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            recyclerChat.post(() -> recyclerChat.smoothScrollToPosition(messages.size() - 1));
        }
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    // ========== 消息数据类 ==========

    static class ChatMessage {
        String text;
        boolean isUser; // true=用户, false=客服
        long timestamp;

        ChatMessage(String text, boolean isUser, long timestamp) {
            this.text = text;
            this.isUser = isUser;
            this.timestamp = timestamp;
        }
    }

    // ========== 聊天 Adapter ==========

    private class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_USER = 0;
        private static final int TYPE_BOT = 1;

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).isUser ? TYPE_USER : TYPE_BOT;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == TYPE_USER) {
                return new UserVH(inflater.inflate(R.layout.item_chat_user, parent, false));
            } else {
                return new BotVH(inflater.inflate(R.layout.item_chat_bot, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

            if (holder instanceof UserVH) {
                ((UserVH) holder).tvText.setText(msg.text);
                ((UserVH) holder).tvTime.setText(sdf.format(new Date(msg.timestamp)));
            } else if (holder instanceof BotVH) {
                ((BotVH) holder).tvText.setText(msg.text);
                ((BotVH) holder).tvTime.setText(sdf.format(new Date(msg.timestamp)));
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class UserVH extends RecyclerView.ViewHolder {
            TextView tvText, tvTime;
            UserVH(@NonNull View v) {
                super(v);
                tvText = v.findViewById(R.id.tv_chat_text);
                tvTime = v.findViewById(R.id.tv_chat_time);
            }
        }

        class BotVH extends RecyclerView.ViewHolder {
            TextView tvText, tvTime;
            BotVH(@NonNull View v) {
                super(v);
                tvText = v.findViewById(R.id.tv_chat_text);
                tvTime = v.findViewById(R.id.tv_chat_time);
            }
        }
    }
}

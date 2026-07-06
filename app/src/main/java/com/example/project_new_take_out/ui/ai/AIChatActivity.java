package com.example.project_new_take_out.ui.ai;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.net.AIClient;
import com.example.project_new_take_out.net.AIApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AI 美食助手聊天页
 */
public class AIChatActivity extends BaseActivity {

    private RecyclerView recyclerChat;
    private EditText etInput;
    private ImageView ivSend;
    private TextView tvNewChat;

    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());

    // 对话历史（API 上下文）
    private final List<Map<String, String>> conversationHistory = new ArrayList<>();
    private boolean isWaiting;

    private static final String SYSTEM_PROMPT = "你是一个专业的美食推荐和外卖助手。你可以："
            + "1. 根据用户口味推荐附近餐厅和菜品"
            + "2. 回答关于美食、烹饪、食材的问题"
            + "3. 帮助用户了解不同菜系的特点"
            + "4. 提供健康饮食建议"
            + "请用热情友好的语气回答，每次回复保持在200字以内。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        recyclerChat = findViewById(R.id.recycler_chat);
        etInput = findViewById(R.id.et_input);
        ivSend = findViewById(R.id.iv_send);
        tvNewChat = findViewById(R.id.tv_new_chat);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter();
        recyclerChat.setAdapter(adapter);

        // 欢迎消息
        addBotMessage("你好！👋 我是 AI 美食助手，由 DeepSeek 驱动。\n\n你可以问我：\n• \"推荐附近好吃的川菜\"\n• \"什么外卖适合减肥吃\"\n• \"今天中午吃什么好\"\n\n随时问我任何美食相关的问题吧！😋");

        // 返回
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 新对话
        tvNewChat.setOnClickListener(v -> {
            messages.clear();
            conversationHistory.clear();
            addBotMessage("新对话开始！有什么美食问题想问我？😊");
        });

        // 发送
        ivSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(text) || isWaiting) return;

        addUserMessage(text);
        etInput.setText("");
        hideKeyboard();
        scrollToBottom();

        // 构建 API 请求
        isWaiting = true;
        callDeepSeekAPI(text);
    }

    @SuppressWarnings("unchecked")
    private void callDeepSeekAPI(String userMessage) {
        AIApiService service = AIClient.getInstance().getApiService();
        String auth = AIClient.getInstance().getAuthHeader();

        // 构建消息历史
        List<Map<String, String>> apiMessages = new ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        apiMessages.add(systemMsg);

        // 添加对话历史（最近10轮）
        int startIdx = Math.max(0, conversationHistory.size() - 20);
        for (int i = startIdx; i < conversationHistory.size(); i++) {
            apiMessages.add(conversationHistory.get(i));
        }

        // 添加当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        apiMessages.add(userMsg);

        // 请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("messages", apiMessages);
        body.put("max_tokens", 500);
        body.put("temperature", 0.7);

        service.chatCompletion(auth, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                isWaiting = false;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.body().get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> choice = choices.get(0);
                            Map<String, String> msg = (Map<String, String>) choice.get("message");
                            String reply = msg.get("content");

                            // 保存对话历史
                            conversationHistory.add(userMsg);
                            Map<String, String> assistantMsg = new HashMap<>();
                            assistantMsg.put("role", "assistant");
                            assistantMsg.put("content", reply);
                            conversationHistory.add(assistantMsg);

                            addBotMessage(reply);
                            scrollToBottom();
                            return;
                        }
                    } catch (Exception e) {
                        addBotMessage("抱歉，解析回复时出错，请重试。");
                    }
                } else {
                    addBotMessage("抱歉，AI 服务暂时不可用（" + response.code() + "），请稍后重试。");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                isWaiting = false;
                addBotMessage("网络连接失败：" + t.getMessage() + "\n请检查网络后重试。");
            }
        });
    }

    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, true));
        adapter.notifyItemInserted(messages.size() - 1);
    }

    private void addBotMessage(String text) {
        messages.add(new ChatMessage(text, false));
        adapter.notifyItemInserted(messages.size() - 1);
    }

    private void scrollToBottom() {
        if (!messages.isEmpty()) {
            recyclerChat.postDelayed(() ->
                    recyclerChat.smoothScrollToPosition(messages.size() - 1), 100);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    // ====== 消息模型 ======

    static class ChatMessage {
        String text;
        boolean isUser;
        ChatMessage(String t, boolean u) { text = t; isUser = u; }
    }

    // ====== Adapter ======

    private class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_USER = 0, TYPE_BOT = 1;

        @Override
        public int getItemViewType(int pos) { return messages.get(pos).isUser ? TYPE_USER : TYPE_BOT; }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (type == TYPE_USER) {
                return new VH(inflater.inflate(R.layout.item_chat_user, parent, false));
            }
            return new VH(inflater.inflate(R.layout.item_chat_bot, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
            TextView tv = h.itemView.findViewById(R.id.tv_chat_text);
            tv.setText(messages.get(pos).text);
        }

        @Override
        public int getItemCount() { return messages.size(); }

        class VH extends RecyclerView.ViewHolder {
            VH(View v) { super(v); }
        }
    }
}

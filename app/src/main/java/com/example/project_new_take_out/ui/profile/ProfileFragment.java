package com.example.project_new_take_out.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.ui.address.AddressManageActivity;
import com.example.project_new_take_out.ui.auth.LoginActivity;
import com.example.project_new_take_out.ui.coupon.CouponListActivity;
import com.example.project_new_take_out.ui.favorite.FavoriteListActivity;
import com.example.project_new_take_out.ui.help.HelpCenterActivity;
import com.example.project_new_take_out.ui.main.MainActivity;
import com.example.project_new_take_out.ui.message.MessageCenterActivity;
import com.example.project_new_take_out.ui.service.CustomerServiceActivity;
import com.example.project_new_take_out.ui.settings.SettingsActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 个人中心（无会员机制）
 */
public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserInfo();
    }

    private void initViews(View view) {
        // 消息图标
        view.findViewById(R.id.iv_message).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MessageCenterActivity.class)));

        // 设置图标
        view.findViewById(R.id.iv_settings_top).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        // 头像点击 → 选择头像
        view.findViewById(R.id.iv_avatar).setOnClickListener(v -> pickAvatar());

        // 用户信息区域 → 未登录点击去登录，已登录点击编辑资料
        view.findViewById(R.id.layout_user_info).setOnClickListener(v -> {
            if (!UserManager.getInstance().isLoggedIn()) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                showEditProfileDialog();
            }
        });

        // 订单入口 → 切到订单Tab（每个入口带对应筛选）
        setupOrderEntry(view, R.id.layout_profile_payment, "pending");
        setupOrderEntry(view, R.id.layout_profile_delivery, "delivering");
        setupOrderEntry(view, R.id.layout_profile_review, "completed");
        setupOrderEntry(view, R.id.layout_profile_refund, "all");
        setupOrderEntry(view, R.id.layout_profile_aftersale, "all");
        setupOrderEntry(view, R.id.layout_profile_orders, "all");

        // 个人服务入口
        setupEntry(view, R.id.layout_profile_address, AddressManageActivity.class);
        setupEntry(view, R.id.layout_profile_coupon, CouponListActivity.class);
        setupEntry(view, R.id.layout_profile_favorite, FavoriteListActivity.class);
        setupEntry(view, R.id.layout_profile_service, CustomerServiceActivity.class);
        setupEntry(view, R.id.layout_profile_settings, SettingsActivity.class);
        setupEntry(view, R.id.layout_profile_help, HelpCenterActivity.class);

        refreshUserInfo();
    }

    private void refreshUserInfo() {
        View view = getView();
        if (view == null) return;

        UserManager um = UserManager.getInstance();

        // 昵称
        TextView tvNickname = view.findViewById(R.id.tv_nickname);
        if (tvNickname != null) {
            tvNickname.setText(um.isLoggedIn() ? um.getNickname() : "点击登录");
        }

        // 手机号提示
        TextView tvPhoneHint = view.findViewById(R.id.tv_phone_hint);
        if (tvPhoneHint != null) {
            if (um.isLoggedIn() && !TextUtils.isEmpty(um.getPhone())) {
                tvPhoneHint.setText(maskPhone(um.getPhone()));
                tvPhoneHint.setVisibility(View.VISIBLE);
            } else if (um.isLoggedIn() && !TextUtils.isEmpty(um.getEmail())) {
                tvPhoneHint.setText(um.getEmail());
                tvPhoneHint.setVisibility(View.VISIBLE);
            } else {
                tvPhoneHint.setVisibility(View.GONE);
            }
        }

        // 头像
        ImageView ivAvatar = view.findViewById(R.id.iv_avatar);
        if (ivAvatar != null) {
            String avatarUri = um.getAvatarUri();
            if (!TextUtils.isEmpty(avatarUri)) {
                Glide.with(this).load(android.net.Uri.parse(avatarUri)).circleCrop().into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_default);
            }
        }
    }

    /**
     * 手机号脱敏：138****5678
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private void pickAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            startActivityForResult(intent, 1001);
        } catch (Exception e) {
            // 无相册应用
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            android.net.Uri uri = data.getData();
            UserManager.getInstance().setAvatarUri(uri.toString());
            ImageView iv = getView() != null ? getView().findViewById(R.id.iv_avatar) : null;
            if (iv != null) {
                Glide.with(this).load(uri).circleCrop().into(iv);
            }
        }
    }

    private void setupEntry(View view, int id, Class<?> cls) {
        view.findViewById(id).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), cls)));
    }

    private void showEditProfileDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(dp2px(16), dp2px(8), dp2px(16), 0);

        final android.widget.EditText etNickname = new android.widget.EditText(requireContext());
        etNickname.setHint("昵称");
        etNickname.setText(UserManager.getInstance().getNickname());
        layout.addView(etNickname);

        final android.widget.EditText etPhone = new android.widget.EditText(requireContext());
        etPhone.setHint("手机号");
        etPhone.setText(UserManager.getInstance().getPhone());
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(etPhone);

        new AlertDialog.Builder(requireContext())
                .setTitle("编辑个人资料")
                .setView(layout)
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (d, w) -> {
                    String name = etNickname.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    if (!name.isEmpty()) UserManager.getInstance().setNickname(name);
                    if (!phone.isEmpty()) UserManager.getInstance().setPhone(phone);
                    refreshUserInfo();
                })
                .show();
    }

    private int dp2px(int dp) {
        return (int) (dp * requireContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setupOrderEntry(View view, int id, String statusFilter) {
        view.findViewById(id).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToOrderTab(statusFilter);
            }
        });
    }
}

package com.example.project_new_take_out.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.project_new_take_out.app.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址管理页面（完整功能版）
 */
public class AddressManageActivity extends BaseActivity {

    private RecyclerView recyclerAddress;
    private AddressAdapter adapter;
    private List<Address> addressList = new ArrayList<>();
    private int nextId = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manage);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        recyclerAddress = findViewById(R.id.recycler_address);
        recyclerAddress.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AddressAdapter();
        recyclerAddress.setAdapter(adapter);

        findViewById(R.id.btn_add_address).setOnClickListener(v -> showAddressDialog(null, -1));

        loadAddresses();
    }

    // ====== 持久化：加载地址 ======

    private void loadAddresses() {
        String userId = com.example.project_new_take_out.utils.UserManager.getInstance().getUserId();
        android.content.SharedPreferences prefs = getSharedPreferences("user_addresses", MODE_PRIVATE);
        String json = prefs.getString(userId, null);

        if (json != null) {
            try {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                com.google.gson.reflect.TypeToken<java.util.List<Address>> token =
                        new com.google.gson.reflect.TypeToken<java.util.List<Address>>() {};
                java.util.List<Address> saved = gson.fromJson(json, token.getType());
                if (saved != null) {
                    addressList.addAll(saved);
                    nextId = addressList.size() + 1;
                }
            } catch (Exception e) {
                initDefaultAddresses();
            }
        } else {
            initDefaultAddresses();
        }
        adapter.notifyDataSetChanged();
    }

    private void initDefaultAddresses() {
        addressList.clear();
        addressList.add(new Address(1, "张三", "13812345678",
                "四川省", "成都市", "武侯区", "天府大道中段688号天府软件园", true));
        addressList.add(new Address(2, "李四", "13987659012",
                "四川省", "成都市", "锦江区", "春熙路99号阳光大厦", false));
        addressList.add(new Address(3, "王五", "13655553456",
                "四川省", "成都市", "高新区", "天府二街368号菁蓉汇", false));
        nextId = 4;
        saveAddresses();
    }

    // ====== 持久化：保存地址 ======

    private void saveAddresses() {
        String userId = com.example.project_new_take_out.utils.UserManager.getInstance().getUserId();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(addressList);
        getSharedPreferences("user_addresses", MODE_PRIVATE)
                .edit().putString(userId, json).apply();
    }

    private void showAddressDialog(Address existingAddr, int position) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp2px(16), dp2px(8), dp2px(16), 0);

        EditText etName = new EditText(this);
        etName.setHint("收货人姓名");
        etName.setText(existingAddr != null ? existingAddr.getContactName() : "");
        layout.addView(etName);

        EditText etPhone = new EditText(this);
        etPhone.setHint("手机号码");
        etPhone.setText(existingAddr != null ? existingAddr.getContactPhone() : "");
        layout.addView(etPhone);

        EditText etDetail = new EditText(this);
        etDetail.setHint("详细地址");
        etDetail.setText(existingAddr != null ? existingAddr.getDetail() : "");
        layout.addView(etDetail);

        String title = existingAddr != null ? "编辑地址" : "添加新地址";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(layout)
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String detail = etDetail.getText().toString().trim();

                    if (name.isEmpty() || phone.isEmpty() || detail.isEmpty()) {
                        Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existingAddr != null) {
                        existingAddr.setContactName(name);
                        existingAddr.setContactPhone(phone);
                        existingAddr.setDetail(detail);
                        adapter.notifyItemChanged(position);
                    } else {
                        Address addr = new Address(nextId++, name, phone,
                                "四川省", "成都市", "武侯区", detail, addressList.isEmpty());
                        addressList.add(addr);
                        adapter.notifyItemInserted(addressList.size() - 1);
                    }
                    saveAddresses();
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    /** 保存默认地址简短文本供首页显示 */
    private void saveDefaultAddress(Address addr) {
        String userId = com.example.project_new_take_out.utils.UserManager.getInstance().getUserId();
        getSharedPreferences("default_address", MODE_PRIVATE)
                .edit().putString(userId, addr.getDetail()).apply();
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.VH> {
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Address addr = addressList.get(pos);
            h.tvName.setText(addr.getContactName() + "  " + addr.getHiddenPhone());
            h.tvAddress.setText(addr.getFullAddress());
            h.tvDefault.setVisibility(addr.isDefault() ? View.VISIBLE : View.GONE);

            h.itemView.setOnClickListener(v -> {
                // 点击地址 → 设为默认并保存
                for (Address a : addressList) a.setDefault(false);
                addr.setDefault(true);
                saveAddresses();
                saveDefaultAddress(addr);
                notifyDataSetChanged();
            });

            h.ivDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(AddressManageActivity.this)
                        .setMessage("确认删除「" + addr.getContactName() + "」的地址？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (d, w) -> {
                            addressList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, addressList.size());
                            saveAddresses();
                        })
                        .show();
            });
        }

        @Override
        public int getItemCount() { return addressList.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvAddress, tvDefault;
            ImageView ivDelete;
            VH(@NonNull View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_address_name);
                tvAddress = v.findViewById(R.id.tv_address_detail);
                tvDefault = v.findViewById(R.id.tv_default_tag);
                ivDelete = v.findViewById(R.id.iv_delete_addr);
            }
        }
    }
}

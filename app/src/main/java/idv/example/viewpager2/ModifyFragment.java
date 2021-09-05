package idv.example.viewpager2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyFragment extends Fragment {
    private FirebaseFirestore db;
    private RadioGroup rgState;
    private EditText etAmount;
    private Button btFinishUpdate, btCancel;
    private Order order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleViews();
    }

    private void findViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        rgState =  view.findViewById(R.id.rgState);
        btFinishUpdate = view.findViewById(R.id.btFinishUpdate);
        btCancel = view.findViewById(R.id.btCancel);
    }

    private void handleViews() {
        // 顯示剛剛點選的 itemview 資料
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("orders");
            if (order != null) {
                String amount = Integer.toString(order.getOrderAmount());
                etAmount.setText(amount);
            }
        }

        // 監聽選擇的訂單狀態
        rgState.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_ready) {
                order.setOrderState("ready");
            } else if (checkedId == R.id.rb_shipped) {
                order.setOrderState("shipped");
            } else if (checkedId == R.id.rb_received) {
                order.setOrderState("received");
            } else {
                order.setOrderState("canceled");
            }
        });

        btFinishUpdate.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            int amount = Integer.parseInt(amountStr);
            order.setOrderAmount(amount);
            modify(order);
        });

        btCancel.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(etAmount);
            navController.popBackStack();
        });
    }

    private void modify(Order order) {
        // 修改指定 ID 的文件
        db.collection("orders").document(order.getOrderId()).set(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "修改成功 with ID: " + order.getOrderId();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // 修改完畢跳轉至訂單列表
                        NavController navController = Navigation.findNavController(etAmount);
                        navController.navigate(R.id.action_ModifyFragment_to_indexFragment);
                    } else {
                        String message = task.getException() == null ?
                                "修改失敗" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
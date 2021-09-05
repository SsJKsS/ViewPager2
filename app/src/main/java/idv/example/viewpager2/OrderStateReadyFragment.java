package idv.example.viewpager2;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderStateReadyFragment extends Fragment {
    private static final String TAG = "ready";
    private AppCompatActivity activity;
    private RecyclerView rvReadyOrder;
    private FirebaseFirestore db;
    private List<Order> orders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        orders = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_state_ready, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        showAllOrders();
    }

    private void findViews(View view) {
        rvReadyOrder = view.findViewById(R.id.rv_ready_order);
    }

    /** 取得所有訂單資訊後顯示 */
    private void showAllOrders() {
        db.collection("orders")
                .whereEqualTo("orderState", "ready")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 先清除舊資料後再儲存新資料
                        if (!orders.isEmpty()) {
                            orders.clear();
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orders.add(document.toObject(Order.class));
                        }
                        // 顯示訂單
                        showOrders();
                    } else {
                        String message = task.getException() == null ?
                                "Not Found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showOrders() {
        OrderAdapter orderAdapter = (OrderAdapter) rvReadyOrder.getAdapter();
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter();
            rvReadyOrder.setAdapter(orderAdapter);
        }
        rvReadyOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderAdapter.setOrders(orders);
    }

    private static class OrderAdapter extends RecyclerView.Adapter<OrderStateReadyFragment.OrderAdapter.MyOrderViewHolder> {
        private List<Order> orders;

        public OrderAdapter() {

        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        private static class MyOrderViewHolder extends RecyclerView.ViewHolder {
            ImageView ivMyOrder;
            TextView tvMyOrderDate;
            TextView tvMyOrderAmount;

            public MyOrderViewHolder(View itemView) {
                super(itemView);
                ivMyOrder = itemView.findViewById(R.id.iv_my_order);
                tvMyOrderDate = itemView.findViewById(R.id.tv_my_order_date);
                tvMyOrderAmount = itemView.findViewById(R.id.tv_my_order_amount);
            }
        }

        @Override
        public OrderStateReadyFragment.OrderAdapter.MyOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_order, parent, false);
            return new OrderStateReadyFragment.OrderAdapter.MyOrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OrderStateReadyFragment.OrderAdapter.MyOrderViewHolder holder, int position) {
            final Order order = orders.get(position);
            holder.tvMyOrderDate.setText(order.getOrderTime() + "");
            holder.tvMyOrderAmount.setText("$" + order.getOrderAmount());

            // 點選會開啟修改頁面
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("orders", order);
                Navigation.findNavController(v)
                        .navigate(R.id.action_indexFragment_to_ModifyFragment, bundle);
            });
        }

        @Override
        public int getItemCount() {
            return orders == null ? 0 : orders.size();
        }
    }

}
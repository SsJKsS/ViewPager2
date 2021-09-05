package idv.example.viewpager2;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class IndexFragment extends Fragment {
    private AppCompatActivity activity;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private int tabIndex;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleTabLayout();
        handleViewPager2();
    }

    private void findViews(View view) {
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager2 = view.findViewById(R.id.ViewPager2);
    }

    private void handleTabLayout() {
        // 監聽當下選擇的頁籤，存值給 tabIndex，讓 ViewPager2 根據得到的值顯示對應的 fragment
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("待出貨")) {
                    tabIndex = 0;
                    Toast.makeText(getActivity(), "待出貨", Toast.LENGTH_SHORT).show();
                } else if (tab.getText().equals("已出貨")) {
                    tabIndex = 1;
                    Toast.makeText(getActivity(), "已出貨", Toast.LENGTH_SHORT).show();
                } else if (tab.getText().equals("已完成")) {
                    tabIndex = 2;
                    Toast.makeText(getActivity(), "已完成", Toast.LENGTH_SHORT).show();
                } else {
                    tabIndex = 3;
                    Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void handleViewPager2() {
        // 將自訂義的 PageAdapter 藉由 viewPager2 載入
        viewPager2.setAdapter(new PageAdapter(activity));
        // 使用 TabLayoutMediator 將 tabLayout & viewPager2 結合
        TabLayoutMediator tab = new TabLayoutMediator(tabLayout, viewPager2, (tab1, position) -> {
            // 根據頁籤位置設定頁籤名稱
            switch (position) {
                case 0:
                    tab1.setText("待出貨");
                    break;
                case 1:
                    tab1.setText("已出貨");
                    break;
                case 2:
                    tab1.setText("已完成");
                    break;
                case 3:
                    tab1.setText("已取消");
                    break;
            }
        });
        // 將剛剛設定好的 TabLayoutMediator 依附在 viewPager2 上
        tab.attach();
    }

    public class PageAdapter extends FragmentStateAdapter {
        public PageAdapter(@NonNull Activity fragmentActivity) {
            super((FragmentActivity) fragmentActivity);
        }

        // 回傳有幾個頁籤
        @Override
        public int getItemCount() {
            return 4;
        }

        // 根據當前所在的頁籤呈現 fragment
        @Override
        public Fragment createFragment(int position) {
            // 獲取當前所在的頁籤位置藉由 setCurrentItem() 設定 position
            if (tabIndex == 0) {
                viewPager2.setCurrentItem(0);
            } else if (tabIndex == 1) {
                viewPager2.setCurrentItem(1);
            } else if (tabIndex == 2) {
                viewPager2.setCurrentItem(2);
            } else {
                viewPager2.setCurrentItem(3);
            }
            // 藉由 position 的值判斷要載入哪個 fragment
            switch (position) {
                case 0:
                    return new OrderStateReadyFragment();
                case 1:
                    return new OrderStateShippedFragment();
                case 2:
                    return new OrderStateReceivedFragment();
                default:
                    return new OrderStateCanceledFragment();
            }
        }

    }

}
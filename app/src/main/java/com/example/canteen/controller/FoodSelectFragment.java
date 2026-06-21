package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.canteen.controller.adapter.FoodSelectAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.canteen.R;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FoodSelectFragment extends Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private TextInputEditText etSearch;
    private MaterialButton btnSearch;
    private MaterialButton btnDone;
    private androidx.recyclerview.widget.RecyclerView rvFoods;

    private FoodRepository foodRepository;
    private FoodSelectAdapter adapter;
    private final PostDraftStore draftStore = PostDraftStore.get();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        foodRepository = FoodRepository.getInstance();

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnDone = view.findViewById(R.id.btnDone);
        rvFoods = view.findViewById(R.id.rvFoods);

        adapter = new FoodSelectAdapter();
        rvFoods.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFoods.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> searchFoods());
        btnDone.setOnClickListener(v -> finishSelect());

        etSearch.setOnEditorActionListener((textView, actionId, event) -> {
            searchFoods();
            return true;
        });

        refreshCurrentSelectionHint();
    }

    private void searchFoods() {
        String keyword = etSearch.getText() == null ? "" : etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(requireContext(), "请输入食物名称", Toast.LENGTH_SHORT).show();
            return;
        }

        disposables.add(
                foodRepository.searchFoodsByName(keyword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showFoods, throwable ->
                                Toast.makeText(requireContext(), "搜索失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    private void showFoods(List<Food> foods) {
        adapter.submit(foods);
        refreshCurrentSelectionHint();
    }

    private void refreshCurrentSelectionHint() {
        List<Food> selected = draftStore.getSelectedFoods();
        btnDone.setText(selected.isEmpty() ? "完成" : "完成（" + selected.size() + "）");
    }

    private void finishSelect() {
        draftStore.setSelectedFoods(adapter.getCurrentSelectedFoods());

        Bundle bundle = new Bundle();
        bundle.putInt("selectedCount", draftStore.getSelectedFoods().size());
        getParentFragmentManager().setFragmentResult("food_select_result", bundle);

        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCurrentSelectionHint();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}
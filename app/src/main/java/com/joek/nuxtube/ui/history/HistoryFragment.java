package com.joek.nuxtube.ui.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.joek.nuxtube.R;
import com.joek.nuxtube.adapters.HistoryAdapter;
import com.joek.nuxtube.data.model.history.History;
import com.joek.nuxtube.data.model.history.HistoryViewModel;
import com.joek.nuxtube.databinding.FragmentHistoryBinding;
import com.joek.nuxtube.listeners.SearchClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistoryFragment extends Fragment implements HistoryAdapter.onLongClickListener{
    private SearchClickListener.OnItemClickListener onItemClickListener;
    private HistoryViewModel mViewModel;
    private RecyclerView recyclerView;
    FragmentHistoryBinding binding;
    HistoryAdapter historyAdapter;
    MaterialCheckBox checkBox;
    MaterialTextView textView, textViewEmpty;
    ImageButton buttonDelete, buttonClear;
    ArrayList<History> historyArrayList = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        checkBox = binding.checkbox;
        textView = binding.textSelection;
        textViewEmpty = binding.textEmpty;
        buttonClear = binding.btnClear;
        buttonDelete = binding.btnDelete;
        recyclerView = binding.recyclerviewHistory;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(getChildFragmentManager(), historyArrayList, getContext(), onItemClickListener);
        recyclerView.setAdapter(historyAdapter);

        mViewModel.getHistory().observe(getViewLifecycleOwner(), histories -> {
            buttonClear.setEnabled(histories.size() > 0);
            if (histories.size() > 0){
                historyArrayList.clear();
                historyArrayList.addAll(histories);
            }else {
                historyArrayList.clear();
            }
            if (histories.size() > 0){
                textViewEmpty.setVisibility(View.GONE);
            }else {
                textViewEmpty.setVisibility(View.VISIBLE);
            }
            historyAdapter.notifyDataSetChanged();
        });
        buttonClear.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Are you sure?")
                    .setNeutralButton("cancel", null)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.clearHistory();
                        }
                    })
                    .show();
        });

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> historyAdapter.check(b));

        buttonDelete.setOnClickListener(view -> {
            for (int i = 0; i < ids.size(); i++) {
                mViewModel.deleteHistory(ids.get(i));
            }
        });
        historyAdapter.getLiveDataIsSelectedAll().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                checkBox.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }else {
                checkBox.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
        });
        historyAdapter.getSelectedItems().observe(getViewLifecycleOwner(), integerBooleanPair -> {
            if (integerBooleanPair.second){
                ids.add(historyArrayList.get(integerBooleanPair.first).getId());
            }else{
                ids.remove(integerBooleanPair.first);
            }

            textView.setText(String.valueOf(ids.size() + " Videos selected"));
        });
        return root;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.onItemClickListener = (SearchClickListener.OnItemClickListener) context;
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (historyAdapter.getShowCheckBoxes()) {
                    historyAdapter.clearSelections();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLongClick() {

    }
}
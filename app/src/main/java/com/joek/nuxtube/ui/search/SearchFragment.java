package com.joek.nuxtube.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joek.nuxtube.activities.MainActivity;
import com.joek.nuxtube.adapters.SearchAdapter;
import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.databinding.FragmentSearchBinding;
import com.joek.nuxtube.listeners.SearchClickListener;

import org.json.JSONException;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchClickListener.OnItemClickListener onItemClickListener;
    private FragmentSearchBinding binding;
    private final ArrayList<SearchItem> searchItems = new ArrayList<>();
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    ProgressBar progressBar;
    SearchViewModel searchViewModel;
    TextView textError;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Log.e("hi","Create");
        searchViewModel = new ViewModelProvider(this, new SearchViewModelFactory()).get(SearchViewModel.class);

        progressBar = binding.loading;
        textError = binding.textError;
        recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchAdapter(getChildFragmentManager(), searchItems, getContext(), onItemClickListener);
        recyclerView.setAdapter(searchAdapter);

        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), searchItem -> {
            searchItems.clear();
            searchItems.addAll(searchItem);
            searchAdapter.notifyItemInserted(searchItems.size() - 1);
            progressBar.setVisibility(View.GONE);
        });
        searchViewModel.getSearchError().observe(getViewLifecycleOwner(), s -> {
            progressBar.setVisibility(View.GONE);
            textError.setText("Error: \n" + s);
            textError.setVisibility(View.VISIBLE);
        });
        try {
            if (getArguments() != null) {
                progressBar.setVisibility(View.VISIBLE);
                searchViewModel.makeRequest(getArguments().getString("query"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.onItemClickListener = (SearchClickListener.OnItemClickListener) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("hi", "destroy");
        binding = null;
    }
}
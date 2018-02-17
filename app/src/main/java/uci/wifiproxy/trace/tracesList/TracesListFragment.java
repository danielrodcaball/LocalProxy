package uci.wifiproxy.trace.tracesList;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uci.wifiproxy.R;
import uci.wifiproxy.data.trace.Trace;

/**
 * Created by daniel on 16/02/18.
 */

public class TracesListFragment extends Fragment implements TracesListContract.View {

    private TracesListContract.Presenter mPresenter;

    private TracesAdapter mAdapter;

    private View mNoTracesView;

    private LinearLayout mTracesView;

    public TracesListFragment() {

    }

    public static TracesListFragment newInstance() {
        return new TracesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TracesAdapter(new ArrayList<Trace>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.traces_list_frag, container, false);

        mNoTracesView = root.findViewById(R.id.noTraces);
        mTracesView = (LinearLayout) root.findViewById(R.id.tracesLL);

        RecyclerView tracesRecycler = (RecyclerView) root.findViewById(R.id.traces_recycler_view);
        tracesRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tracesRecycler.setLayoutManager(layoutManager);
        tracesRecycler.setAdapter(mAdapter);
        tracesRecycler.addItemDecoration(new DividerItemDecoration(
                tracesRecycler.getContext(), layoutManager.getOrientation()));

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(tracesRecycler);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTraces();
            }
        });

        setHasOptionsMenu(true);

        return root;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) return;

        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTraces(List<Trace> traces) {
        mAdapter.replaceData(traces);
        mNoTracesView.setVisibility(View.GONE);
        mTracesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTraceDetailUi(String traceId) {

    }

    @Override
    public void showNoTraces() {
        mNoTracesView.setVisibility(View.VISIBLE);
        mTracesView.setVisibility(View.GONE);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(TracesListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private class TracesAdapter extends RecyclerView.Adapter<TracesAdapter.ViewHolder> {

        private List<Trace> mDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView appIcon;
            public TextView url;
            public TextView consumption;
            public TextView appName;
            public ImageButton expandTrace;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(getContext(), "bla", Toast.LENGTH_LONG).show();
                        return false;
                    }
                });

                this.appIcon = view.findViewById(R.id.packageLogo);
                this.url = view.findViewById(R.id.url);
                this.consumption = view.findViewById(R.id.consumption);
                this.appName = view.findViewById(R.id.applicationName);
                this.expandTrace = view.findViewById(R.id.expand_trace);
            }
        }

        public TracesAdapter(List<Trace> dataSet) {
            mDataSet = dataSet;
        }

        private void setList(List<Trace> dataSet) {
            mDataSet = dataSet;
        }

        public void replaceData(List<Trace> dataSet) {
            setList(dataSet);
            notifyDataSetChanged();
        }

        @Override
        public TracesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trace_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TracesAdapter.ViewHolder holder, int position) {
            Trace trace = mDataSet.get(position);
            PackageManager packageManager = getContext().getPackageManager();
            try {
                holder.appIcon.setImageDrawable(packageManager.getApplicationIcon(trace.getSourceApplication()));
            } catch (PackageManager.NameNotFoundException e) {
                holder.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            holder.consumption.setText(String.format("%.2f", trace.getBytesSpent() / 2048.0) + " MB");
            holder.url.setText(trace.getRequestedUrl());
            holder.appName.setText(trace.getSourceApplication());
            holder.expandTrace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tag = (String) view.getTag();
                    if (tag.equals("close")) {
                        holder.url.setMaxLines(Integer.MAX_VALUE);
                        ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_close_trace));
                        view.setTag("open");
                    }
                    else{
                        holder.url.setMaxLines(1);
                        ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_trace));
                        view.setTag("close");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }
}

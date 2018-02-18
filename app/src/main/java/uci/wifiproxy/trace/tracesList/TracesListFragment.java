package uci.wifiproxy.trace.tracesList;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uci.wifiproxy.R;
import uci.wifiproxy.data.trace.Trace;
import uci.wifiproxy.proxy.ProxyActivity;

/**
 * Created by daniel on 16/02/18.
 */

public class TracesListFragment extends Fragment implements TracesListContract.View {

    private TracesListContract.Presenter mPresenter;

    private TracesAdapter mAdapter;

    private View mNoTracesView;

    private LinearLayout mTracesView;

    private String FILTER_KEY = "";

    private boolean SORT_BY_CONSUMPTION = false;

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
//        tracesRecycler.setHasFixedSize(true);
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
                mPresenter.loadTraces(FILTER_KEY, SORT_BY_CONSUMPTION);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.trace_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FILTER_KEY = query;
                mPresenter.loadTraces(FILTER_KEY, SORT_BY_CONSUMPTION);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                FILTER_KEY = "";
                mPresenter.loadTraces(FILTER_KEY, SORT_BY_CONSUMPTION);
                return true;
            }
        });

        // Get the search close button image view
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et = (EditText) searchView.findViewById(R.id.search_src_text);
                //Clear the text from EditText view
                et.setText("");
                //Clear query
                searchView.setQuery("", false);
                FILTER_KEY = "";
                mPresenter.loadTraces(FILTER_KEY, SORT_BY_CONSUMPTION);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sortByConsumption:
                if (item.isChecked()){
                    item.setChecked(false);
                    SORT_BY_CONSUMPTION = false;
                }
                else {
                    item.setChecked(true);
                    SORT_BY_CONSUMPTION = true;
                }
                mPresenter.loadTraces(FILTER_KEY, SORT_BY_CONSUMPTION);
                break;
            case R.id.clear_all:
                mPresenter.deleteAllTraces();
                break;
        }
        return true;
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
    public void showSuccessfullyAddedAsFirewallRuleMessage() {
        Snackbar.make(getActivity().findViewById(R.id.contentFrame),
                getResources().getString(R.string.successfully_added_as_firewallrule),
                Snackbar.LENGTH_LONG).show();
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
            public TextView date;

            private ViewHolder(View view) {
                super(view);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu menu = new PopupMenu(getContext(), view);
                        menu.getMenu().add(getString(R.string.add_as_firewall_rule)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setMessage(getString(R.string.firewallRule_rule_tv));

                                final EditText editRule = new EditText(getContext());
                                editRule.setText(url.getText());
                                alertDialog.setView(editRule);

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.add),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.dismiss();
                                                mPresenter.addAsFirewallRule(editRule.getText().toString(), appName.getText().toString());
                                            }
                                        });
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                alertDialog.dismiss();
                                            }
                                        });

                                alertDialog.show();
                                return false;
                            }
                        });
                        menu.show();
                        return false;
                    }
                });

                this.appIcon = view.findViewById(R.id.packageLogo);
                this.url = view.findViewById(R.id.url);
                this.consumption = view.findViewById(R.id.consumption);
                this.appName = view.findViewById(R.id.applicationName);
                this.date = view.findViewById(R.id.date);
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
            holder.appName.setText((trace.getAppName().equals(Trace.UNKNOWN_APP_NAME)) ? trace.getSourceApplication() : trace.getAppName());

            Date date = new Date(trace.getDatetime());
            holder.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));

            holder.expandTrace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tag = (String) view.getTag();
                    if (tag.equals("close")) {
                        holder.url.setMaxLines(Integer.MAX_VALUE);
                        ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_close_trace));
                        view.setTag("open");
                    } else {
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

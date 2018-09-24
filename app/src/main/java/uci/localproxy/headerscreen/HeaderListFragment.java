package uci.localproxy.headerscreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import uci.localproxy.R;
import uci.localproxy.data.header.Header;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by daniel on 30/08/18.
 */

public class HeaderListFragment extends Fragment implements HeaderListContract.View {

    private HeaderListContract.Presenter mPresenter;

    private HeaderListAdapter mListAdapter;

    private View mNoHeadersView;

    private View mHeadersView;

    public HeaderListFragment() {
        // Requires empty public constructor
    }

    public static HeaderListFragment newInstance() {
        return new HeaderListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new HeaderListAdapter(new ArrayList<Header>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.header_list_frag, container, false);

        //set up headers view
        ListView listView = root.findViewById(R.id.headers_list_view);
        listView.setAdapter(mListAdapter);
        mHeadersView = root.findViewById(R.id.headersLL);

        //set up no headers view
        mNoHeadersView = root.findViewById(R.id.noHeaders);
        TextView noHeadersTv = root.findViewById(R.id.noHeadersAdd);
        noHeadersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewHeader();
            }
        });

        //Set up floating action button
        FloatingActionButton fab = getActivity()
                .findViewById(R.id.fab_add_header);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewHeader();
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void setPresenter(HeaderListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showHeaders(List<Header> headers) {
        mListAdapter.replaceData(headers);
        mHeadersView.setVisibility(View.VISIBLE);
        mNoHeadersView.setVisibility(View.GONE);
    }

    @Override
    public void showAddEditHeaderDialog(@Nullable final Header header) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle((header == null) ? getString(R.string.new_h) : getString(R.string.edit_h));
        View root = LayoutInflater.from(getContext()).inflate(R.layout.addeditheader_dialog, null, false);
        final EditText hName = root.findViewById(R.id.header_name);
        final EditText hValue = root.findViewById(R.id.header_value);
        if (header != null){
            hName.setText(header.getName());
            hValue.setText(header.getValue());
        }
        alertDialog.setView(root);
        alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Header h;
                if (header == null){
                    h = Header.newHeader(hName.getText().toString(), hValue.getText().toString());
                }
                else{
                    h = Header.newHeader(header.getId(), hName.getText().toString(), hValue.getText().toString());
                }
                mPresenter.saveHeader(h);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void showNoHeaders() {
        mHeadersView.setVisibility(View.GONE);
        mNoHeadersView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_header_message));
    }

    @Override
    public void showHeaderNameAlreadyExistError() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_error);
        alertDialog.setTitle(getString(R.string.error_title));
        alertDialog.setMessage(getString(R.string.header_name_already_exist_message));
        alertDialog.show();
    }

    @Override
    public void showHeaderRemovedMessage() {
        showMessage(getString(R.string.header_removed_message));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    private class HeaderListAdapter extends BaseAdapter {

        private List<Header> mHeaderList;

        public HeaderListAdapter(List<Header> headerList) {
            this.mHeaderList = headerList;
        }

        private void setList(List<Header> headerList) {
            mHeaderList = headerList;
        }

        public void replaceData(List<Header> headerList) {
            setList(headerList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mHeaderList.size();
        }

        @Override
        public Header getItem(int position) {
            return mHeaderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            public LinearLayout headerTextLL;
            public TextView headerText;
            public Button removeButton;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.header_list_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.headerText = rowView.findViewById(R.id.headerText);
                viewHolder.removeButton = rowView.findViewById(R.id.removeHeaderBtn);
                viewHolder.headerTextLL = rowView.findViewById(R.id.headerTextLL);
                rowView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            final Header header = getItem(position);
            viewHolder.headerText.setText(header.toString());
            viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.removeHeader(header.getId());
                }
            });
            viewHolder.headerTextLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.editHeader(header);
                }
            });
            return rowView;
        }
    }
}

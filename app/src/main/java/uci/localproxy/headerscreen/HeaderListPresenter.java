package uci.localproxy.headerscreen;

import android.support.annotation.NonNull;

import java.util.List;

import uci.localproxy.data.header.Header;
import uci.localproxy.data.header.HeaderDataSource;

/**
 * Created by daniel on 30/08/18.
 */

public class HeaderListPresenter implements HeaderListContract.Presenter {

    private final HeaderDataSource mHeadersDataSource;

    @NonNull
    private final HeaderListContract.View mView;

    public HeaderListPresenter(HeaderListContract.View view){
        mHeadersDataSource = HeaderDataSource.newInstance();
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void loadHeaders() {
        mHeadersDataSource.getAllHeaders(new HeaderDataSource.LoadHeadersCallback() {
            @Override
            public void onHeadersLoaded(List<Header> headers) {
                if (!mView.isActive()) return;
                mView.showHeaders(headers);
            }

            @Override
            public void onDataNoAvailable() {
                if (!mView.isActive()) return;
                mView.showNoHeaders();
            }
        });
    }

    @Override
    public void addNewHeader() {
        mView.showAddEditHeaderDialog(null);
    }

    @Override
    public void editHeader(@NonNull Header requestedHeader) {
        mView.showAddEditHeaderDialog(requestedHeader);
    }

    @Override
    public void saveHeader(@NonNull Header header) {
        mHeadersDataSource.saveUpdateHeader(header, new HeaderDataSource.SaveUpdateHeaderCallback() {
            @Override
            public void onHeaderSaved() {
                loadHeaders();
                mView.showSuccessfullySavedMessage();
            }

            @Override
            public void onHeaderNameAlreadyExist() {
                mView.showHeaderNameAlreadyExistError();
            }
        });
    }

    @Override
    public void removeHeader(@NonNull String headerId) {
        mHeadersDataSource.removeHeader(headerId);
        loadHeaders();
        mView.showHeaderRemovedMessage();
    }

    @Override
    public void onDestroy() {
        mHeadersDataSource.releaseResources();
    }

    @Override
    public void start() {
        loadHeaders();
    }
}

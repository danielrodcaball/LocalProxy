package uci.wifiproxy.trace.tracesList;

import android.support.annotation.NonNull;

import java.util.List;

import uci.wifiproxy.data.trace.Trace;
import uci.wifiproxy.data.trace.TraceDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 16/02/18.
 */

public class TracesListPresenter implements TracesListContract.Presenter {

    private TraceDataSource mTraceDataSource;
    private TracesListContract.View mView;

    public TracesListPresenter(@NonNull TracesListContract.View view){
        mTraceDataSource = TraceDataSource.newInstance();
        mView = checkNotNull(view, "view cannot be null");

        mView.setPresenter(this);
    }

    @Override
    public void loadTraces() {
        loadTraces(true);
    }

    private void loadTraces(final boolean showLoadingUi){
        if (showLoadingUi){
            mView.setLoadingIndicator(true);
        }

        mTraceDataSource.getAllTraces(new TraceDataSource.LoadTracesCallback() {
            @Override
            public void onTracesLoaded(List<Trace> traces) {
                if (!mView.isActive()) return;

                mView.showTraces(traces);

                if (showLoadingUi){
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onDataNoAvailable() {
                if (!mView.isActive()) return;
                mView.showNoTraces();
                mView.setLoadingIndicator(false);
            }
        });

    }

    @Override
    public void openTracesDetails(@NonNull Trace requestedTrace) {

    }

    @Override
    public void onDestroy() {
        mTraceDataSource.releaseResources();
    }

    @Override
    public void start() {
        loadTraces();
    }
}

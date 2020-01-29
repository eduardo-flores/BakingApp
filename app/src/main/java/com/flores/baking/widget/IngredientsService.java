package com.flores.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.flores.baking.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class IngredientsService extends IntentService {

    public static final String ACTION_UPDATE_WIDGETS = "com.flores.baking.widget.action.update_baking_app_widgets";

    public IngredientsService() {
        super("IngredientsService");
    }

    /**
     * Starts this service to perform UpdateWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, IngredientsService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            }
        }
    }

    /**
     * Handle action UpdateWidgets in the provided background thread
     */
    private void handleActionUpdateWidgets() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] widgetsIds = widgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidget.class));
        widgetManager.notifyAppWidgetViewDataChanged(widgetsIds, R.id.lv_recipe_widget);
        BakingAppWidget.updateWidgets(this, widgetManager, widgetsIds);
    }
}

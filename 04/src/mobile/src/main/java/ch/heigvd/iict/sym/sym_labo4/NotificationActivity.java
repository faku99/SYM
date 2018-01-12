package ch.heigvd.iict.sym.sym_labo4;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import ch.heigvd.iict.sym.wearcommon.Constants;

public class NotificationActivity extends AppCompatActivity {

    // UI components
    private Button simpleNotification = null;
    private Button actionsNotification = null;
    private Button wearNotification = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if(getIntent() != null)
            onNewIntent(getIntent());

        // Link button with UI
        simpleNotification = findViewById(R.id.simpleNotification);
        actionsNotification = findViewById(R.id.actionsNotification);
        wearNotification = findViewById(R.id.wearNotification);

        // Send a simple notification to the phone and, if presents, to the Android Wear watch
        simpleNotification.setOnClickListener(view -> {

            // Set the notification IDs to be able to find it after
            int notificationId = 664;
            String id = "emceeSimpleNotification";

            // Build a pending intent with a callback to display the passed message in a Toast
            // when the notification is opened
            PendingIntent pendingIntent = createPendingIntent(notificationId, getString(R.string.noCoke));

            // Use the notification builder to create the given notification based on the pending intent
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id);
            notificationBuilder
                .setSmallIcon(R.drawable.ic_alert_white_18dp)
                .setContentTitle(getString(R.string.wantCoke))
                .setContentIntent(pendingIntent);

            // Get an instance of the NotificationManager service and pass the notification to it
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, notificationBuilder.build());
        });

        // Send a notification with some defined actions to the phone and, if presents, to the Android Wear watch
        actionsNotification.setOnClickListener(view -> {

            // Set the notification IDs to be able to find it after
            int notificationId = 665;
            String id = "emceeActionsNotification";

            // Build an intent with a specific action associated to it.
            // This action is highly based on the documentation.
            // https://developer.android.com/training/wearables/notifications/creating.html
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);

            // Build the HEIG-VD location for the map.
            Uri geoUri = Uri.parse(getString(R.string.heigLocation));

            // Set the location for the map intent
            mapIntent.setData(geoUri);

            // Create the pending intent
            PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);

            // Build a pending intent with a callback to display the passed message in a Toast
            // when the notification is opened
            PendingIntent pendingIntent = createPendingIntent(notificationId, getString(R.string.dontWantToBuy));

            // Use the notification builder to create the given notification based on the pending intent
            // and add an action to it to open Google Maps with the action is pressed
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id);
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_alert_white_18dp)
                    .setContentTitle(getString(R.string.buyRandomStuff))
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_alert_white_18dp,
                            getString(R.string.goHere), mapPendingIntent);

            // Get an instance of the NotificationManager service and pass the notification to it
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, notificationBuilder.build());
        });

        // Send a notification to the Android Wear watch. The phone won't see this notification as it's a special
        // notification that only an Android Wear compatible device can understand
        wearNotification.setOnClickListener(view -> {

            // Set the notification IDs to be able to find it after
            int notificationId = 666;
            String id = "emceeWearNotification";

            // Build an intent with a specific action associated to it.
            // This action is highly based on the documentation.
            // https://developer.android.com/training/wearables/notifications/creating.html
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);

            // Build the HEIG-VD location for the map.
            Uri geoUri = Uri.parse(getString(R.string.heigLocation));

            // Set the location for the map intent
            mapIntent.setData(geoUri);

            // Create the pending intent
            PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);

            // Create a special wearable extender with the map action so it is only visible on Android Wear products
            NotificationCompat.WearableExtender wearableExtender =
                    new NotificationCompat.WearableExtender()
                            .setHintHideIcon(true)
                            .addAction(new NotificationCompat.Action(
                                    R.drawable.ic_alert_white_18dp,
                                    getString(R.string.wantDoYouWant),
                                    mapPendingIntent
                            ));


            // Build a pending intent with a callback to display the passed message in a Toast
            // when the notification is opened
            PendingIntent pendingIntent = createPendingIntent(notificationId, getString(R.string.dontWantToBuy));

            // Use the notification builder to create the given notification based on the pending intent
            // and add the extender to add the wearable action
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id);
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_alert_white_18dp)
                    .setContentTitle(getString(R.string.buyRandomStuff))
                    .setContentIntent(pendingIntent)
                    .extend(wearableExtender)
                    .build();

            // Get an instance of the NotificationManager service and pass the notification to it
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, notificationBuilder.build());
        });
    }

    /*
     *  Code fourni pour les PendingIntent
     */

    /*
     *  Method called by system when a new Intent is received
     *  Display a toast with a message if the Intent is generated by
     *  createPendingIntent method.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null) return;
        if(Constants.MY_PENDING_INTENT_ACTION.equals(intent.getAction()))
            Toast.makeText(this, "" + intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method used to create a PendingIntent with the specified message
     * The intent will start a new activity Instance or bring to front an existing one.
     * See parentActivityName and launchMode options in Manifest
     * See https://developer.android.com/training/notify-user/navigation.html for TaskStackBuilder
     * @param requestCode The request code
     * @param message The message
     * @return The pending Intent
     */
    private PendingIntent createPendingIntent(int requestCode, String message) {
        Intent myIntent = new Intent(NotificationActivity.this, NotificationActivity.class);
        myIntent.setAction(Constants.MY_PENDING_INTENT_ACTION);
        myIntent.putExtra("msg", message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(myIntent);

        return stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}

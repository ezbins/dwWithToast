package tw.idv.ezbins.downloadwithtoast;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {
    private Button dwBtn;
    private File dwFolder;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dwBtn = (Button) findViewById(R.id.downbtn);
        dwBtn.setOnClickListener(dwloadListen);
    }

    View.OnClickListener dwloadListen = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri dwPath = Uri.parse("https://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg");
            final long downloadReference;
            dwFolder = new File(Environment.getExternalStorageDirectory()
                    + "/Download");

            if (!dwFolder.exists()) {
                dwFolder.mkdirs();
            }
            //Starting download
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(dwPath);
            //Setting title of request
            request.setTitle("Image Download");
            //Setting description of request
            request.setDescription("Android Data download using DownloadManager.");

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "colors.jpg");

            //Enqueue download and save the referenceId
            downloadReference = downloadManager.enqueue(request);

            //Check DownloadStatus and
            //Query the download manager about downloads that have been requested.
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        DownloadManager.Query downloadQuery = new DownloadManager.Query();
                        downloadQuery.setFilterById(downloadReference);
                        Cursor cursor = downloadManager.query(downloadQuery);
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(index)) {
                                Toast.makeText(getApplicationContext(), "Download Success", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            };
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    });
}
package com.example.absol.riffa;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AudioRecord extends AppCompatActivity {
    private MyVisualizer visualizerView;
    private Visualizer mVisualizer;

    private static final String TAG = "Patrik";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference mStorageRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private String uniqueID;

    private ProgressDialog mProgress;

    private Recording newRec;
    private ImageButton mBtn;
    private boolean mStartRecording;

    private Dialog mDialog;
    private Dialog mSaveDialog;
    private Button btnDelete, btnSave, btnPlay;
    private TextView txtClose;
    private SeekBar seek;

    private TextView titleEdit;
    private TextView genreEdit;
    private String mTitle;
    private String mGenre;
    private int mLength;
    private String mDate;


    private  boolean checkAndRequestPermissions() {
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int permissionModifyAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permissionModifyAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MODIFY_AUDIO_SETTINGS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Recording services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                            showDialogOK("Recording and Modifying Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                            finish();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        //String url = newRec.getLink();

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setText("Play");
                mVisualizer.setEnabled(false);
            }
        });
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void uploadAudio(final String title, final int length, final String genre, final String date) {

        final String userId = auth.getCurrentUser().getUid();
        uniqueID = UUID.randomUUID().toString();

        Uri file = Uri.fromFile(new File(mFileName));
        StorageReference rec = mStorageRef.child(uniqueID);

        mProgress.setMessage("Saving audio ...");
        mProgress.show();

        rec.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        newRec = new Recording(title, length, genre, date, downloadUrl.toString());

                        myRef.child("Users").child(userId).child("Recordings").push().setValue(newRec);

                        mProgress.dismiss();
                        Toast.makeText(AudioRecord.this, "Recording complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        mProgress.setMessage("Recording failed");
                        mProgress.dismiss();
                        Toast.makeText(AudioRecord.this, "Recording failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();

        //uploadAudio();

        mRecorder = null;
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_record);
        setupActionBar();

        if(checkAndRequestPermissions()) {

            mBtn = findViewById(R.id.imageButton);
            mDialog = new Dialog(this);
            mSaveDialog = new Dialog(this);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            mProgress = new ProgressDialog(this);

            // Record to the external cache directory for visibility
            mFileName = getExternalCacheDir().getAbsolutePath();
            mFileName += "/audiorecordtest.3gp";


            mStartRecording = true;
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecord(mStartRecording);
                    if (mStartRecording) {
                        v.setBackgroundResource(R.drawable.ic_stop_filled);
                        //setText("Stop recording");
                    } else {
                        showPopup(v);
                        v.setBackgroundResource(R.drawable.ic_record_disc);
                        //setText("Start recording");
                    }
                    mStartRecording = !mStartRecording;
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void showPopup(View v) {
        startPlaying();
        mPlayer.setLooping(false);

        mDialog.setContentView(R.layout.popup_recording);
        visualizerView = (MyVisualizer) mDialog.findViewById(R.id.visualizer);

        txtClose = mDialog.findViewById(R.id.dialogclose);
        btnDelete = mDialog.findViewById(R.id.btnDelete);
        btnSave = mDialog.findViewById(R.id.btnSave);
        btnPlay = mDialog.findViewById(R.id.btnPlay);
        seek = mDialog.findViewById(R.id.popupSeekbar);

        seek.setVisibility(ProgressBar.VISIBLE);
        seek.setProgress(0);
        seek.setMax(mPlayer.getDuration());

        setupVisualizerFxAndUI();
        mVisualizer.setEnabled(true);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayer.isPlaying()) {
                    startPlaying();
                    setupVisualizerFxAndUI();
                    mVisualizer.setEnabled(true);
                    btnPlay.setText("Stop");
                } else if(mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mVisualizer.setEnabled(false);
                    btnPlay.setText("Play");
                }
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                mVisualizer.setEnabled(false);
                mDialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                mVisualizer.setEnabled(false);
                mDialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mVisualizer.setEnabled(false);
                showSavePopup(v);
            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

    }

    public void showSavePopup(View v) {
        mSaveDialog.setContentView(R.layout.popup_saverecording);

        titleEdit = mSaveDialog.findViewById(R.id.saveTitle);
        genreEdit = mSaveDialog.findViewById(R.id.saveGenre);
        Button saveBtn = mSaveDialog.findViewById(R.id.btnSaveSave);

        mLength = mPlayer.getDuration();
        mDate = getCurrentDate();

        txtClose = mSaveDialog.findViewById(R.id.dialogclose);

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveDialog.dismiss();
                onStop();
                Toast.makeText(AudioRecord.this, "Audio deleted...", Toast.LENGTH_SHORT).show();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = titleEdit.getText().toString();
                mGenre = genreEdit.getText().toString();
                Log.d(TAG, "onClick: " + mTitle + mGenre);

                titleEdit.setError(null);
                genreEdit.setError(null);
                View focusView = null;
                boolean cancel = false;

                if(TextUtils.isEmpty(mTitle)) {
                    titleEdit.setError("This title is too short");
                    focusView = titleEdit;
                    cancel = true;
                } else if(TextUtils.isEmpty(mGenre)) {
                    genreEdit.setError("This genre is too short");
                    focusView = genreEdit;
                    cancel = true;
                }

                if(cancel)
                    focusView.requestFocus();
                else {
                    uploadAudio(mTitle,mLength,mGenre,mDate);
                    mSaveDialog.dismiss();
                }
            }
        });

        mSaveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSaveDialog.show();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }




    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

}

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
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;

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
    private Chronometer chronometer;

    private Dialog mDialog;
    private Dialog mSaveDialog;
    private Button btnDelete, btnSave;
    private ImageButton btnPlay;
    private TextView txtClose;
    private SeekBar seek;
    private Handler mSeekbarUpdateHandler;
    private Runnable mUpdateSeekbar;
    boolean stopHandler = false;

    private TextView titleEdit;
    private TextView genreEdit;
    private String mTitle;
    private String mGenre;
    private int mLength;
    private String mDate;

    private ArrayList<ClickCounter> recentCcList = new ArrayList<>();

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

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        seek.setMax(mPlayer.getDuration());
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setImageResource(R.drawable.ic_play_filled);
                mVisualizer.setEnabled(false);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
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
        mRecorder.setMaxDuration(30000);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if(what==MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording();
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    mBtn.setBackgroundResource(R.drawable.ic_record_disc);
                    showPopup(chronometer);
                    mStartRecording = true;
                }
            }
        });
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

                        DatabaseReference pushedRef = myRef.child("Users").child(userId).child("Recordings").push();
                        String key = pushedRef.getKey();

                        newRec = new Recording(title, length, genre, date, downloadUrl.toString(), key, currentUser.getDisplayName(), uniqueID);
                        pushedRef.setValue(newRec);

                        long time = System.currentTimeMillis();
                        ClickCounter cc = new ClickCounter(key, userId, time);
                        myRef.child("ClickCounters").push().setValue(cc);

                        addRecent(cc);

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
            chronometer = findViewById(R.id.chronometer);
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
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        //setText("Stop recording");
                    } else {
                        chronometer.stop();
                        chronometer.setBase(SystemClock.elapsedRealtime());
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
        Log.d(TAG, "onStop: called onstop");

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        super.onStop();
    }

    public void showPopup(View v) {

        stopHandler = false;
        mDialog.setContentView(R.layout.popup_recording);
        visualizerView = (MyVisualizer) mDialog.findViewById(R.id.visualizer);

        txtClose = mDialog.findViewById(R.id.dialogclose);
        btnDelete = mDialog.findViewById(R.id.btnDelete);
        btnSave = mDialog.findViewById(R.id.btnSave);
        btnPlay = mDialog.findViewById(R.id.btnPlay);
        seek = mDialog.findViewById(R.id.popupSeekbar);

        mSeekbarUpdateHandler = new Handler();
        mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                if(!stopHandler) {
                    seek.setProgress(mPlayer.getCurrentPosition());
                    mSeekbarUpdateHandler.postDelayed(this, 50);
                }
            }
        };
        startPlaying();
        mPlayer.setLooping(false);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mPlayer.seekTo(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        setupVisualizerFxAndUI();
        mVisualizer.setEnabled(true);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayer.isPlaying()) {
                    startPlaying();
                    setupVisualizerFxAndUI();
                    mVisualizer.setEnabled(true);
                    btnPlay.setImageResource(R.drawable.ic_stop_filled);
                } else if(mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mVisualizer.setEnabled(false);
                    btnPlay.setImageResource(R.drawable.ic_play_filled);
                }
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                mVisualizer.setEnabled(false);
                stopHandler = true;
                mDialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                mVisualizer.setEnabled(false);
                stopHandler = true;
                mDialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mVisualizer.setEnabled(false);
                stopHandler = true;
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
                } else if(mTitle.length() > 13) {
                    titleEdit.setError("This title is too long");
                    focusView = titleEdit;
                    cancel = true;
                } else if(mGenre.length() > 15) {
                    genreEdit.setError("This genre is too long");
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

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroyed");
        super.onDestroy();
    }

    private void addRecent(ClickCounter cc) {
        recentCcList.clear();
        myRef.child("Recent").push().setValue(cc);

        Query recentQuery = myRef.child("Recent").orderByChild("time");
        recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    recentCcList.add(ds.getValue(ClickCounter.class));
                    if(recentCcList.size()>25) {
                        Log.d(TAG, "onDataChange: calling delete recent");
                        deleteRecent();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void deleteRecent() {
        Query deleteQuery = myRef.child("Recent").orderByChild("key").equalTo(recentCcList.get(0).getKey());
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

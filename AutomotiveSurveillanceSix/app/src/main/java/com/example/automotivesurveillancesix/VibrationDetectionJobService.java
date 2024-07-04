package com.example.automotivesurveillancesix;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class VibrationDetectionJobService extends JobService {

    private static final String TAG = "VibrationDetectionJob";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        // Start VibrationDetectionService to handle vibration detection and capturing images
        Intent serviceIntent = new Intent(this, VibrationDetectionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Schedule the next job
        scheduleNextJob();

        return false; // Job finished, no need to reschedule
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job stopped");
        return true; // Reschedule the job if it was stopped
    }

    private void scheduleNextJob() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), VibrationDetectionJobService.class);
                    JobInfo jobInfo = new JobInfo.Builder(MainActivity.JOB_ID, componentName)
                            .setRequiresDeviceIdle(true)
                            .setRequiresCharging(false)
                            .setPeriodic(15 * 60 * 1000) // every 15 minutes
                            .build();

                    JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    int resultCode = jobScheduler.schedule(jobInfo);
                    if (resultCode == JobScheduler.RESULT_SUCCESS) {
                        Log.d(TAG, "Job scheduled successfully");
                    } else {
                        Log.d(TAG, "Job scheduling failed");
                    }
                }
            }
        }, 15 * 60 * 1000); // delay for 15 minutes
    }
}

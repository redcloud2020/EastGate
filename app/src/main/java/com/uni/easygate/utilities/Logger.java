package com.uni.easygate.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uni.easygate.ui.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Emad Yehya on 6/11/2017.
 */

public class Logger {
    private static Logger  _instance;
    private static FileOutputStream writer;
    private static Context ctx;
    private static boolean in_use = false;

    public boolean logs_found = false;
    public boolean finished = false;
    public int success_logs;
    public int failed_logs = 0;

    private static RequestQueue queue;

    private Logger() {
        init_reader();
    }

    private void init_reader() {
        try {
            File root =  ctx.getExternalFilesDir(null);

            if(!root.exists()) {
                root.mkdirs();
            }

            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            File file = new File(root, date + ".txt");
            writer = new FileOutputStream(file, true);
        } catch (Exception e) {
            Log.e("emad", "COULD NOT CREATE FILE");
            e.printStackTrace();

        }
    }

    public void write_line(String line) {
        line += "\n";
        if(writer == null)
            init_reader();

        try {
            writer.write(line.getBytes());
        } catch (Exception e) {

        }
    }

    public void end_logging() {
        if(writer != null) {
            try {
                writer.close();
            } catch (Exception e) {}
            writer = null;
        }
    }

    public void upload_logs() {
        if(in_use) {
            return;
        }

        in_use = true;
        end_logging(); // to avoid weird stuff happening due to open writer

        File root =  ctx.getExternalFilesDir(null);
        if(!root.exists()) {
            root.mkdirs();
        }

        logs_found = false;
        finished = false;
        failed_logs = 0;
        success_logs = 0;

        int max_ind = 0;
        for(int i = 0; i < 10; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1*i);
            SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");
            date_format.setTimeZone(cal.getTimeZone());
            String file_name = date_format.format(cal.getTime());
            final File file = new File(root, file_name + ".txt");

            if(file.exists()) max_ind = i;
        }

        // try looking through last at most 10 days.
        update_logs(root, 0, max_ind);

    }

    public void update_logs(final File root, final int index, final int max_index) {

        Log.d("emad", "Now for index: " + String.valueOf(index));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1*index);
        SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");
        date_format.setTimeZone(cal.getTimeZone());
        String file_name = date_format.format(cal.getTime());
        final File file = new File(root, file_name + ".txt");

        Log.d("emad", "The date: " + file_name);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            final Map<String, String> params = new HashMap<>();
            params.put("log_data", text.toString());

            logs_found = true;
            StringRequest req = new StringRequest(Request.Method.POST, "http://deerail.com/add_emad_log", new Response.Listener<String>() {
                @Override
                public void onResponse(String resp) {
                    Log.d("emad", "got response");
                    Log.d("emad", "result: " + resp);
                    // OK! Delete the file

                    if("1".equals(resp.toString())) {
                        success_logs++;
                        file.delete();
                    } else {
                        failed_logs++;
                    }
                    // flags
                    if(index == max_index) {
                        finished = true;
                        in_use = false;
                    }
                }
            }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("emad", "got error");
                        failed_logs++;
                    }

            }) {
                @Override
                public Map<String, String> getParams() {
                    Log.d("emad", "It asked for params.");
                    return params;
                }
            };
            queue.add(req);
            if(index == max_index) {
                return;
            }
            update_logs(root, index+1, max_index);
        }
        catch (IOException e) {
            Log.d("emad", "error caught!");
            if(index == max_index) {
                finished = true;
                in_use = false;
                return;
            }
            update_logs(root, index+1, max_index);
        }

    }

    public synchronized static Logger getInstance(Context context)
    {
        if (_instance == null)
        {
            ctx = context;
            _instance = new Logger();
            queue = Volley.newRequestQueue(ctx);
        }
        return _instance;
    }

}

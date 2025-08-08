package com.example.webscraper;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageAdapter.OnDownloadClickListener {

    private EditText urlInput;
    private Button scrapeButton;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private final List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.url_input);
        scrapeButton = findViewById(R.id.scrape_button);
        recyclerView = findViewById(R.id.recycler_view);

        setupRecyclerView();

        scrapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlInput.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url; // Prepend https:// if no protocol is found
                }
                new ScrapeTask().execute(url);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageAdapter(this, imageUrls, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDownloadClick(String imageUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setTitle("Image Download");
        request.setDescription("Downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // Save the file to the public "Downloads" folder
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(imageUrl).getLastPathSegment());

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);
        Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT).show();
    }

    private class ScrapeTask extends AsyncTask<String, Void, List<String>> {
        private String error = null;

        @Override
        protected List<String> doInBackground(String... urls) {
            String url = urls[0];
            List<String> foundImageUrls = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(url).get();
                Elements images = doc.select("img");
                if (images.isEmpty()) {
                    error = "No images found on the page.";
                    return null;
                }
                for (Element img : images) {
                    String imageUrl = img.absUrl("src");
                    if (!imageUrl.isEmpty()) {
                        foundImageUrls.add(imageUrl);
                    }
                }
                return foundImageUrls;
            } catch (IOException e) {
                e.printStackTrace();
                error = "Error: Could not connect to the URL or page structure is different.";
                return null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                error = "Error: Invalid URL. Make sure it starts with http:// or https:// and is correct.";
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            imageUrls.clear();
            if (result != null) {
                imageUrls.addAll(result);
            } else if (error != null) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
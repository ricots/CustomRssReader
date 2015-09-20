package com.example.josh.techcrunchrss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity implements ResultsCallback, AdapterView.OnItemClickListener {

    PlaceholderFragment taskFragment;
    ListView articlesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        //swipeView.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                //android.R.color.holo_orange_light, android.R.color.holo_red_light);


        articlesListView = (ListView) findViewById(R.id.articlesListView);
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        articlesListView.setAdapter(adp);
        articlesListView.setOnItemClickListener(this);

        //swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            //@Override
            //public void onRefresh() {

                //new TechCrunchTask().execute(feedUrl);

                //swipeView.setRefreshing(true);
                //( new Handler()).postDelayed(new Runnable() {
                    //@Override
                    //public void run() {
                        //swipeView.setRefreshing(false);

                //    }
                //}, 4000);
            //}
        //});
/*
        articlesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            */
/*
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });
*/

        if (savedInstanceState == null) {
            taskFragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction().add(taskFragment, "MyFragment").commit();
        }else{
           taskFragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentByTag("MyFragment");
            }

        taskFragment.startTask();

        articlesListView = (ListView) findViewById(R.id.articlesListView);

        }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(final ArrayList<HashMap<String, String>> results) {

        articlesListView.setAdapter(new MyAdapter(this, results));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyAdapter adapter = (MyAdapter)articlesListView.getAdapter();
        Uri uri = Uri.parse(adapter.dataSource.get(position).get("link"));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public static class PlaceholderFragment extends Fragment{

        TechCrunchTask downloadTask;
        ResultsCallback callback;

        public PlaceholderFragment(){

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            callback = (ResultsCallback) activity;
            if (downloadTask!=null)
            {
                downloadTask.onAttach(callback);
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);
        }
        public void startTask(){
            if(downloadTask!=null){
                downloadTask.cancel(true);
            }
            else {
                downloadTask = new TechCrunchTask(callback);
                downloadTask.execute();
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            callback = null;
            if (downloadTask!=null)
            {
                downloadTask.onDetach();
            }
        }
    }

    public static class TechCrunchTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>{

        ResultsCallback callback = null;

        public TechCrunchTask(ResultsCallback callback){
            this.callback = callback;
        }

        public void onAttach(ResultsCallback callback) {
            this.callback = callback;
        }

        public  void onDetach() {
            callback = null;
        }

        @Override
        protected void onPreExecute() {
            if (callback!=null)
            {
                callback.onPreExecute();
            }

        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params){

            String downloadURL="http://www.legislature.mi.gov/documents/publications/RssFeeds/billupdate.xml";
            ArrayList<HashMap<String, String>> results = new ArrayList<>();
            try {
                URL url = new URL(downloadURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                results = processXML(inputStream);

            } catch (Exception e) {
                L.m(e + "");
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {


            if (callback!=null)
            {
                callback.onPostExecute(result);
            }
        }

        public ArrayList<HashMap<String, String>> processXML(InputStream inputStream) throws Exception {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse(inputStream);
            Element rootElement = xmlDocument.getDocumentElement();
            L.m("" + rootElement.getTagName());
            NodeList itemsList = rootElement.getElementsByTagName("item");
            NodeList itemChildren = null;
            Node currentItem = null;
            Node currentChild = null;
            int count = 0;
            ArrayList<HashMap<String, String>> results = new ArrayList<>();
            HashMap<String, String> currentMap = null;


            for (int i = 0; i<itemsList.getLength(); i++){
                currentItem = itemsList.item(i);
                // L.m("" + currentItem.getNodeName());
                itemChildren = currentItem.getChildNodes();

                currentMap = new HashMap<>();
                for (int j = 0; j < itemChildren.getLength(); j++){
                    currentChild = itemChildren.item(j);
                    // L.m("" + currentChild.getNodeName());
                    if (currentChild.getNodeName().equalsIgnoreCase("title")){
                        // L.m(currentChild.getTextContent());
                        currentMap.put("title", currentChild.getTextContent());
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("pubDate")){
                        // L.m(currentChild.getTextContent());
                        currentMap.put("pubDate", currentChild.getTextContent());
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("description")){
                        // L.m(currentChild.getTextContent());
                        currentMap.put("description", currentChild.getTextContent());
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("link")){
                        currentMap.put("link", currentChild.getTextContent());
                    }
                    /*
                        if (currentChild.getNodeName().equalsIgnoreCase("media:thumbnail")){
                        count++;
                        if (count == 2){
                            currentMap.put("imageURL", currentChild.getAttributes().item(0).getTextContent());
                            // L.m(currentChild.getAttributes().item(0).getTextContent());
                        }
                    }
                     */

                }
                if (currentMap !=null && !currentMap.isEmpty()){
                    results.add(currentMap);
                }
                count = 0;
            }
            return results;

        }
    }
}

interface ResultsCallback {

    public void onPreExecute();
    public void onPostExecute(ArrayList<HashMap<String, String>> results);

}

class MyAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> dataSource = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;


    public MyAdapter(Context context, ArrayList<HashMap<String, String>> dataSource){
        this.dataSource = dataSource;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyHolder holder = null;
        if (row == null) {
            row = layoutInflater.inflate(R.layout.custom_row, parent, false);
            holder = new MyHolder(row);
            row.setTag(holder);

        } else {
            holder = (MyHolder) row.getTag();

        }

        HashMap<String, String> currentItem = dataSource.get(position);
        holder.articleTitleText.setText(currentItem.get("title"));
        holder.articlePublishedDateText.setText(currentItem.get("pubDate"));
        //holder.articleImage.setImageURI(Uri.parse(currentItem.get("imageURL")));
        holder.articleDescriptionText.setText(currentItem.get("description"));
        //holder.articleLink.setText(currentItem.get("link"));

        return row;
    }
}

class MyHolder {

    TextView articleTitleText;
    TextView articlePublishedDateText;
    //ImageView articleImage;
    TextView articleDescriptionText;
    //TextView articleLink;

    public MyHolder(View view) {

        articleTitleText = (TextView) view.findViewById(R.id.articleTitleText);
        articlePublishedDateText = (TextView) view.findViewById(R.id.articlePublishedDate);
        //articleImage = (ImageView) view.findViewById(R.id.articleImage);
        articleDescriptionText = (TextView) view.findViewById(R.id.articleDescriptionText);
        //articleLink = (TextView)view.findViewById(R.id.articleLink);

    }

}











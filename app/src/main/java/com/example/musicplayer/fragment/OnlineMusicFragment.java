package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.ParseJsonUtil;
import com.example.musicplayer.bean.Album;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.RvAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OnlineMusicFragment extends Fragment {
    private RecyclerView onlineMusicList;
    private List<Album> albums = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onlinemusic_fragment, container, false);
        onlineMusicList = view.findViewById(R.id.onlineMusic_list);
        getData();
        initRv();
        return view;
    }


    private void getData (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=json&type=1&offset=0&size=50");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    Log.i("HttpURLConnection.GET","开始连接");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        Log.i("HttpURLConnection.GET", "请求成功");
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        ParseJsonUtil parseJsonUtil = new ParseJsonUtil();
                        String albumImgPath = parseJsonUtil.parseJsonObj(response.toString());
                        Album album = new Album();
                        album.setAlbumImgPath(albumImgPath);
                        album.setTop1("top1");
                        album.setTop2("top2");
                        album.setTop3("top3");
                        System.out.println(album);
                        albums.add(album);
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }else {
                        Log.i("HttpURLConnection.GET", "请求失败");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    initRv();
                    break;
            }
        }
    };

    private void setAlbum(){

    }

    private void initRv(){
        RvAdapter adapter = new RvAdapter(albums);
        onlineMusicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        onlineMusicList.setAdapter(adapter);
    }
}
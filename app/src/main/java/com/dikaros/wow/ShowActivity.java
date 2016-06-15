package com.dikaros.wow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dikaros.wow.bean.Friend;
import com.dikaros.wow.bean.ImMessage;
import com.dikaros.wow.net.asynet.AsyNet;
import com.dikaros.wow.net.asynet.NormalAsyNet;
import com.dikaros.wow.service.WebSocketService;
import com.dikaros.wow.util.AlertUtil;
import com.dikaros.wow.util.SimpifyUtil;
import com.dikaros.wow.util.Util;
import com.dikaros.wow.util.annotation.FindView;
import com.dikaros.wow.view.RecyclerViewDivider;
import com.google.gson.Gson;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyNet.OnNetStateChangedListener<String> {


    @FindView(R.id.rcv_friend)
    RecyclerView rcvFriend;

    @FindView(R.id.srl_friend)
    SwipeRefreshLayout srlMain;

    List<Friend> friends;


    RelativeLayout blankBoard;

    //朋友适配器
    FriendAdapter friendAdapter;

    //网络工具
    NormalAsyNet net;

    MyReceiver receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        SimpifyUtil.findAll(this);
        //设置toolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        friends = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出菜单


            }
        });

        receiver = new MyReceiver();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rcvFriend.setLayoutManager(new LinearLayoutManager(this));
        friendAdapter = new FriendAdapter();
        rcvFriend.setAdapter(friendAdapter);
        friendAdapter.setInnerItemListener(new InnerItemListener() {
            @Override
            public void onClick(View v, int index) {
                Intent intent = new Intent(ShowActivity.this,ChatActivity.class);
                intent.putExtra("friend",friends.get(index));
                intent.putExtra("start_position",friends.get(index).getNewMessage());
                friends.get(index).setNewMessage(0);
                friendAdapter.notifyDataSetChanged();
                startActivity(intent);
            }

            @Override
            public void onLongClick(View v, int index) {

            }
        });

        srlMain.setColorSchemeColors(Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN);

        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                accessNet();
            }
        });
        accessNet();
        setTitle("Wow");
        rcvFriend.addItemDecoration(new RecyclerViewDivider(this,
                RecyclerViewDivider.VERTICAL_LIST));

    }


    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WebSocketService.ACTION_WEBSOCKET_ON_MESSAGE);
        filter.addAction(WebSocketService.ACTION_WEBSOCKET_CLOSE);
        filter.addAction(WebSocketService.ACTION_WEBSOCKET_OPEN);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        Intent s = new Intent(this, WebSocketService.class);
        s.putExtra(WebSocketService.START_WEBSOCKET, true);
        s.putExtra(WebSocketService.USER_ID,Config.userId);
        startService(s);
        super.onResume();

    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    public void accessNet() {
        if (net != null) {
            net.cancel(true);
            net = null;
        }

        net = new NormalAsyNet(Config.HTTP_GET_FRIEND, "jsonFile", "{\"userId\":" + Config.userId + "}", AsyNet.NetMethod.POST);
        Log.e("wow",Config.userId+"");
        net.setOnNetStateChangedListener(this);
        net.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.show, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_music) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_video) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id==R.id.nav_log_out){
            Util.setPreference(this,"user_msg",null);
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void beforeAccessNet() {
        srlMain.setEnabled(false);

    }

    @Override
    public void afterAccessNet(String result) {
        srlMain.setRefreshing(false);
        srlMain.setEnabled(true);
        Log.e("wow", result + "--");
        if (result != null || result.equals("[]")) {
            try {
                JSONArray array = new JSONArray(result);
                Gson gson = new Gson();
                friends.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.getJSONObject(i);
                    Friend f = gson.fromJson(o.toString(), Friend.class);
                    Log.e("wow_friend", f.toString());
                    friends.add(f);
                }
                Log.e("friend_count",friends.size()+"");
                rcvFriend.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void whenException(Throwable t) {
        srlMain.setRefreshing(false);
        srlMain.setEnabled(true);
        Log.e("wow", t.getMessage() + "--");


    }

    @Override
    public void onProgress(Integer progress) {

    }


    interface InnerItemListener{
        public void onClick(View v,int index);
        public void onLongClick(View v,int index);
    }

    /**
     * Adapter
     */
    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

        InnerItemListener innerItemListener;

        public void setInnerItemListener(InnerItemListener innerItemListener) {
            this.innerItemListener = innerItemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(ShowActivity.this).inflate(R.layout.list_cell_friend, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            //设置数据
            Friend friend = friends.get(position);
            //加载头像
            Picasso.with(ShowActivity.this).load(Config.HTTP_AVATAR_ADDRESS + "/image/avator/" + friend.getFriendId() + ".png").error(R.drawable.icon).into(holder.civ_friend);
            //设置名字
            holder.tvFriendName.setText((friend.getFriendMark() == null || friend.getFriendMark().equals("")) ? friend.getFriendName() : friend.getFriendMark());
            //设置个性签名
            holder.tvLastMsg.setText(friend.getFriendMessage() == null ? "他还没有设置个性签名" : friend.getFriendMessage());
            if (innerItemListener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerItemListener.onClick(v,position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        innerItemListener.onLongClick(v,position);
                        return true;
                    }
                });
            }

            if (friend.getNewMessage()>0){
                holder.tvMessageCount.setText(friend.getNewMessage()+"");
//                badgeView.show();
                holder.tvMessageCount.setAlpha(1f);
            }else {
                holder.tvMessageCount.setAlpha(0f);
            }
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @FindView(R.id.civ_friend)
            CircleImageView civ_friend;

            @FindView(R.id.tv_friend_name)
            TextView tvFriendName;

            @FindView(R.id.tv_friend_last_message)
            TextView tvLastMsg;

            @FindView(R.id.tv_message_count)
            TextView tvMessageCount;


            public ViewHolder(View itemView) {
                super(itemView);
                SimpifyUtil.findAll(this, itemView);
            }
        }
    }


    /**
     * 广播接收器
     */
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WebSocketService.ACTION_WEBSOCKET_CLOSE:
                    AlertUtil.toastMess(ShowActivity.this,"与服务器断开连接");

                    break;
                case WebSocketService.ACTION_WEBSOCKET_ON_MESSAGE:
                    ImMessage message = (ImMessage) intent.getSerializableExtra(WebSocketService.WEBSOCKET_MESSAGE);
//                    Config.addToReveivedMap(message);
                    for (int i=0;i<friends.size();i++){
                        Friend f = friends.get(i);
                        if (f.getFriendId()==message.getSenderId()){
//                            a
                            f.addMessage();
                            friendAdapter.notifyItemChanged(i);
                        }
                    }

                    break;
                case WebSocketService.ACTION_WEBSOCKET_OPEN:
                    //连接成功时
                    AlertUtil.toastMess(ShowActivity.this,"连接上服务器");

                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION:
//                    AlertUtil.simpleAlertDialog(ShowActivity.this,"网络状态改变", NetUtil.isNetworkAvailable(ShowActivity.this)+"");
//                //如果可以连接网络
//                if (!connecting && NetUtil.isNetworkAvailable(MainShowActivity.this)) {
//                    if (!NetUtil.isWifi(MainShowActivity.this)) {
//                        AlertUtil.simpleAlertDialog(MainShowActivity.this, "提醒", "当前处于移动网络状态，可能产生大量流量");
//                    }
//                    Intent service = new Intent(MainShowActivity.this, WebSocketService.class);
//                    service.putExtra(WebSocketService.START_WEBSOCKET, true);
//                    startService(service);
//                }
                    break;

                default:
                    break;
            }


        }
    }



}

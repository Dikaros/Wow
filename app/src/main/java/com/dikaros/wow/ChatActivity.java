package com.dikaros.wow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dikaros.wow.bean.Friend;
import com.dikaros.wow.bean.ImMessage;
import com.dikaros.wow.service.WebSocketService;
import com.dikaros.wow.util.AlertUtil;
import com.dikaros.wow.util.SimpifyUtil;
import com.dikaros.wow.util.annotation.FindView;
import com.dikaros.wow.util.annotation.OnClick;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Friend friend;

    MyReceiver receiver;

    @FindView(R.id.et_conversation)
    EditText etConversation;

    @FindView(R.id.btn_conversation_send)
    Button btnSend;

    @FindView(R.id.srl_chat)
    SwipeRefreshLayout srlFriend;

    @FindView(R.id.rcv_chat)
    RecyclerView rcvChat;

    List<ImMessage> reveivedMessages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        friend= (Friend) getIntent().getSerializableExtra("friend");
        SimpifyUtil.findAll(this);
        btnSend.setEnabled(false);
        receiver = new MyReceiver();
        //如果有了信息
        if (Config.reveivedMap.containsKey(friend.getFriendId())){
            //添加进来
            reveivedMessages.addAll(Config.reveivedMap.get(friend.getFriendId()));
        }
        rcvChat.setLayoutManager(new LinearLayoutManager(this));

        etConversation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    btnSend.setEnabled(true);
                }else {
                    btnSend.setEnabled(false);
                }
            }
        });
    }


    @OnClick(R.id.btn_conversation_send)
    public void sendMessage(View v){
        Intent intent = new Intent(this,WebSocketService.class);
        ImMessage message = new ImMessage(Config.userId,friend.getFriendId(),System.currentTimeMillis(),etConversation.getText().toString(),1);
        intent.putExtra(WebSocketService.SEND_MESSAGE,message.toJson());
        startService(intent);
        etConversation.setText("");
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

    /**
     * 广播接收器
     */
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new Gson();
            switch (intent.getAction()) {
                case WebSocketService.ACTION_WEBSOCKET_CLOSE:
                    AlertUtil.toastMess(ChatActivity.this,"断开服务器");
                    break;
                case WebSocketService.ACTION_WEBSOCKET_ON_MESSAGE:
                    ImMessage msg = (ImMessage) intent.getSerializableExtra(WebSocketService.WEBSOCKET_MESSAGE);
                    //解析传来的数据
                    if (msg.getSenderId()==friend.getFriendId()){
                        AlertUtil.toastMess(ChatActivity.this,"收到："+msg.getMsg());
                    }



                    break;
                case WebSocketService.ACTION_WEBSOCKET_OPEN:
                    //连接成功时


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


    /**
     * Adapter
     */
    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

//        InnerItemListener innerItemListener;

//        public void setInnerItemListener(InnerItemListener innerItemListener) {
//            this.innerItemListener = innerItemListener;
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = null;
            switch (viewType){
                case 1:
                    v= LayoutInflater.from(ChatActivity.this).inflate(R.layout.list_cell_chat_left, parent, false);

                    break;
                case 2:
                    v= LayoutInflater.from(ChatActivity.this).inflate(R.layout.list_cell_chat_right, parent, false);

                    break;
            }
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

//            //设置数据
//            Friend friend = friends.get(position);
//            //加载头像
//            Picasso.with(ShowActivity.this).load(Config.HTTP_AVATAR_ADDRESS + "/image/avator/" + friend.getFriendId() + ".png").error(R.drawable.icon).into(holder.civ_friend);
//            //设置名字
//            holder.tvFriendName.setText((friend.getFriendMark() == null || friend.getFriendMark().equals("")) ? friend.getFriendName() : friend.getFriendMark());
//            //设置个性签名
//            holder.tvLastMsg.setText(friend.getFriendMessage() == null ? "他还没有设置个性签名" : friend.getFriendMessage());
//            if (innerItemListener!=null){
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        innerItemListener.onClick(v,position);
//                    }
//                });
//                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        innerItemListener.onLongClick(v,position);
//                        return true;
//                    }
//                });
//            }
        }

        @Override
        public int getItemCount() {
            return reveivedMessages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @FindView(R.id.civ_friend)
            CircleImageView civ_friend;

            @FindView(R.id.tv_friend_name)
            TextView tvFriendName;

            @FindView(R.id.tv_friend_last_message)
            TextView tvLastMsg;

            public ViewHolder(View itemView) {
                super(itemView);
                SimpifyUtil.findAll(this, itemView);
            }
        }
    }
}

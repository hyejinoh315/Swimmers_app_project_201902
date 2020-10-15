package com.example.myswimming.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myswimming.R;
import com.example.myswimming.items.VideoInfo;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static ArrayList<VideoInfo> videoInfoArrayList;
    private OnItemClickListener listener;
    Context context;

    //각 데이터 항목에 뷰에 대한 참조를 제공
    //복잡한 데이터 항목은 항목당 둘 이상의 보기가 필요할 수 있다
    //뷰 홀더에서 데이터 항목의 모든 보기에 대한 엑세스 권한을 제공
    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        //이 경우 각 데이터 항목은 단지 문자열이다
        protected TextView title, uri;

        //생성자
        public VideoViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.videoName);

            view.setOnCreateContextMenuListener(this);
        }

        //implements 항목에 의해 호출됨
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            /*꾹 눌렀을 때 메뉴가 등장하게 해라*/
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "PLAY");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "DELETE");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1001:
                        int position = getAdapterPosition();
                        if (listener != null && position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(videoInfoArrayList.get(position));
                        }
                        //수정항목
                        break;
                    case 1002:
                        int position1 = getAdapterPosition();
                        if (listener != null && position1 != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position1);
                        }
                        break;
                }
                return true;
            }
        };
    }

    /**/
    public interface OnItemClickListener {
        void onItemClick(VideoInfo videoInfo);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }/**/

    // 데이터 집합 종류에 따른 적절한 생성자를 제공한다
    public VideoAdapter(ArrayList<VideoInfo> videoInfoArrayList) {
        this.videoInfoArrayList = videoInfoArrayList;
    }

    // 새로운 뷰를 생성, 레이아웃 관리자에 의해 호출된다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새 보기 생성, 뷰홀더의 유형은 어댑터 클래스에 선언된 유형과 일치해야 한다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row, parent, false);
        context = parent.getContext();
        return new VideoViewHolder(v);
    }

    // 뷰의 내용을 변경, 레이아웃 관리자에 의해 호출된다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - 이 위치에서 데이터 세트의 요소 가져오고, 뷰의 내용을 그 요소로 대체
        VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

        videoViewHolder.title.setText(videoInfoArrayList.get(position).title);
    }

    // 데이터 세트의 길이(크기)를 반환한다
    @Override
    public int getItemCount() {
        if (videoInfoArrayList.size() != 0){
        return videoInfoArrayList.size();}
        return 0;
    }


}

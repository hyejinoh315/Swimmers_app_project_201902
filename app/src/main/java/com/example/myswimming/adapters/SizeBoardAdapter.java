package com.example.myswimming.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.myswimming.R;
import com.example.myswimming.items.SizeBoardInfo;
import java.util.ArrayList;

public class SizeBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static ArrayList<SizeBoardInfo> boardInfoArrayList;
    private OnItemClickListener listener;

    //각 데이터 항목에 뷰에 대한 참조를 제공
    //복잡한 데이터 항목은 항목당 둘 이상의 보기가 필요할 수 있다
    //뷰 홀더에서 데이터 항목의 모든 보기에 대한 엑세스 권한을 제공
    public class BoardViewHolder extends RecyclerView.ViewHolder {
        //이 경우 각 데이터 항목은 단지 문자열이다
        protected TextView title, time, indexNo;

        //생성자
        public BoardViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title_size); // (bN+pN+pS+"size")
            time = view.findViewById(R.id.time_size);
            indexNo = view.findViewById(R.id.index_size);
            //뷰홀더에 담기는 항목들

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(boardInfoArrayList.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SizeBoardInfo boardInfo);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // 데이터 집합 종류에 따른 적절한 생성자를 제공한다
    public SizeBoardAdapter(ArrayList<SizeBoardInfo> boardInfoArrayList) {
        this.boardInfoArrayList = boardInfoArrayList;
    }

    // 새로운 뷰를 생성, 레이아웃 관리자에 의해 호출된다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새 보기 생성, 뷰홀더의 유형은 어댑터 클래스에 선언된 유형과 일치해야 한다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.size_row, parent, false);

        return new BoardViewHolder(v);
    }

    // 뷰의 내용을 변경, 레이아웃 관리자에 의해 호출된다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - 이 위치에서 데이터 세트의 요소 가져오고, 뷰의 내용을 그 요소로 대체
        BoardViewHolder boardViewHolder = (BoardViewHolder) holder;

        boardViewHolder.title.setText(boardInfoArrayList.get(position).title);
        boardViewHolder.time.setText(boardInfoArrayList.get(position).time);
        boardViewHolder.indexNo.setText(boardInfoArrayList.get(position).indexNo);
    }

    // 데이터 세트의 길이(크기)를 반환한다
    @Override
    public int getItemCount() {
        return boardInfoArrayList.size();
    }
}

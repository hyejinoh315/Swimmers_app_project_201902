package com.example.myswimming.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.myswimming.R;
import com.example.myswimming.items.DayInfo;
import java.util.ArrayList;

public class DayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static ArrayList<DayInfo> dayInfoArrayList;
    private OnItemClickListener listener;
    Context context;

    //각 데이터 항목에 뷰에 대한 참조를 제공
    //복잡한 데이터 항목은 항목당 둘 이상의 보기가 필요할 수 있다
    //뷰 홀더에서 데이터 항목의 모든 보기에 대한 엑세스 권한을 제공
    public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        //이 경우 각 데이터 항목은 단지 문자열이다
        protected TextView tvTitle, tvDate, tvMemo;

        //생성자
        public DayViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.title_day);
            tvDate = view.findViewById(R.id.date_day);
            tvMemo = view.findViewById(R.id.memo_day);

            view.setOnCreateContextMenuListener(this);
        }

        //implements 항목에 의해 호출됨
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            /*꾹 눌렀을 때 메뉴가 등장하게 해라*/
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "EDIT");
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
                            listener.onItemClick(dayInfoArrayList.get(position));
                        }
                        //수정항목
                        break;
                    case 1002:
                        int position1 = getAdapterPosition();
                        if (listener != null && position1 != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position1);
                        }
                        /*삭제하려냐는 다이얼로그 띄우기
                        dayInfoArrayList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), dayInfoArrayList.size());*/
                        break;
                }
                return true;
            }
        };
    }

    /**/
    public interface OnItemClickListener {
        void onItemClick(DayInfo dayInfo);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }/**/

    // 데이터 집합 종류에 따른 적절한 생성자를 제공한다
    public DayAdapter(ArrayList<DayInfo> dayInfoArrayList) {
        this.dayInfoArrayList = dayInfoArrayList;
    }

    // 새로운 뷰를 생성, 레이아웃 관리자에 의해 호출된다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새 보기 생성, 뷰홀더의 유형은 어댑터 클래스에 선언된 유형과 일치해야 한다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_row, parent, false);
        context = parent.getContext();
        return new DayViewHolder(v);
    }

    // 뷰의 내용을 변경, 레이아웃 관리자에 의해 호출된다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - 이 위치에서 데이터 세트의 요소 가져오고, 뷰의 내용을 그 요소로 대체
        DayViewHolder dayViewHolder = (DayViewHolder) holder;

        dayViewHolder.tvTitle.setText(dayInfoArrayList.get(position).title);
        dayViewHolder.tvDate.setText(dayInfoArrayList.get(position).date);
        dayViewHolder.tvMemo.setText(dayInfoArrayList.get(position).memo);
    }

    // 데이터 세트의 길이(크기)를 반환한다
    @Override
    public int getItemCount() {
        if (dayInfoArrayList.size() != 0){
        return dayInfoArrayList.size();}
        return 0;
    }


}

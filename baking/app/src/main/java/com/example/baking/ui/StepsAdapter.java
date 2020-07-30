package com.example.baking.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.baking.R;
import com.example.baking.data.Step;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepAdapterViewHolder> {

    private List<Step> mStepData;
    private Context mContext;

    private int mItemSelected = -1;

    public int getPositionStep(int id){
        if(mStepData == null || mStepData.size()==0){
            return -1;
        }
        int retorno = -1;
        for(int i = 0; i<mStepData.size(); i++){
            if(mStepData.get(i).getId()==id){
                retorno = i;
            }
        }

        return retorno;
    }


    public void setmItemSelected(int itemSelected){
        mItemSelected = itemSelected;
        notifyDataSetChanged();
    }

    private final StepAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface StepAdapterOnClickHandler {
        void onClick(Step stepClicked, View view);
    }

    /**
     * Creates a StepAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public StepsAdapter(StepAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }
    /**
     * Cache of the children views for a forecast list item.
     */
    public class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTvStepNumber;
        public final TextView mTvShortDescription;




        public StepAdapterViewHolder(View view) {
            super(view);
            mTvShortDescription = (TextView) view.findViewById(R.id.tv_step_short);
            mTvStepNumber = (TextView) view.findViewById(R.id.tv_number_step) ;
            view.setOnClickListener(this);
        }
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Step stepClicked = mStepData.get(adapterPosition) ;
            mClickHandler.onClick(stepClicked, mTvStepNumber);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *            ///      link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieAdapterViewHolder that holds the View for each list item
     */
    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.item_step;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        @SuppressLint("ResourceType")
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new StepAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param stepAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(StepAdapterViewHolder stepAdapterViewHolder, int position) {
        stepAdapterViewHolder.mTvStepNumber.setText(String.valueOf(mStepData.get(position).getId()+1));
        stepAdapterViewHolder.mTvStepNumber.setVisibility(View.INVISIBLE);
       stepAdapterViewHolder.mTvShortDescription.setText(mStepData.get(position).getShortDescription());
       if(position == mItemSelected){
           stepAdapterViewHolder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.round_button_selected));
                   //.setBackgroundColor(mContext.getResources().getColor(R.color.step_selected_color));
       }    else{
           stepAdapterViewHolder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.round_button));
                   //.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
       }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mStepData) return 0;
        return mStepData.size();
    }

    /**
     * This method is used to set the weather forecast on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param stepData The new weather data to be displayed.
     */
    public void setStepsData(List<Step> stepData) {
        mStepData = stepData;
        if(mStepData!=null)
            notifyDataSetChanged();
    }
}
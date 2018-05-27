package com.amirarcane.lockscreen.andrognito.pinlockview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medicnet.android.R;

/**
 * Created by aritraroy on 31/05/16.
 */
public class PinLockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NUMBER = 0;
    private static final int VIEW_TYPE_DELETE = 1;
    private static final int VIEW_TYPE_CANCEL = 2;

    private CustomizationOptionsBundle mCustomizationOptionsBundle;
    private OnNumberClickListener mOnNumberClickListener;
    private OnDeleteClickListener mOnDeleteClickListener;
    private int mPinLength;
    private int BUTTON_ANIMATION_DURATION = 150;

    private int[] mKeyValues;

    private Typeface mTypeface = null;
    private int greenLightColor;
    private int whiteColor;
    private boolean isCancelable;
    private Context context;

    public PinLockAdapter() {
        this.mKeyValues = getAdjustKeyValues(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
    }

    public void setTypeFace(Typeface typeFace) {
        mTypeface = typeFace;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        context = parent.getContext();
        greenLightColor = ContextCompat.getColor(context, R.color.green_light);
        whiteColor = ContextCompat.getColor(context, R.color.white);

        if (viewType == VIEW_TYPE_NUMBER) {
            View view = inflater.inflate(R.layout.layout_number_item, parent, false);
            viewHolder = new NumberViewHolder(view, mTypeface);
        } else if (viewType == VIEW_TYPE_DELETE) {
            View view = inflater.inflate(R.layout.layout_delete_item, parent, false);
            viewHolder = new DeleteViewHolder(view);
        } else if (viewType == VIEW_TYPE_CANCEL) {
            View view = inflater.inflate(R.layout.layout_delete_item, parent, false);
            viewHolder = new CancelViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NUMBER) {
            NumberViewHolder vh1 = (NumberViewHolder) holder;
            configureNumberButtonHolder(vh1, position);
        } else if (holder.getItemViewType() == VIEW_TYPE_DELETE) {
            DeleteViewHolder vh2 = (DeleteViewHolder) holder;
            configureDeleteButtonHolder(vh2);
        } else if (holder.getItemViewType() == VIEW_TYPE_CANCEL) {
            CancelViewHolder vh3 = (CancelViewHolder) holder;
//            configureDeleteButtonHolder(vh2);
        }
    }

    private void configureNumberButtonHolder(NumberViewHolder holder, int position) {
        if (holder != null) {
            if (position == 9) {
                holder.mNumberButton.setVisibility(View.GONE);
            } else {
                holder.mNumberButton.setText(String.valueOf(mKeyValues[position]));
                holder.mNumberButton.setVisibility(View.VISIBLE);
                holder.mNumberButton.setTag(mKeyValues[position]);
            }

            if (mCustomizationOptionsBundle != null) {
                holder.mNumberButton.setTextColor(mCustomizationOptionsBundle.getTextColor());
                if (mCustomizationOptionsBundle.getButtonBackgroundDrawable() != null) {
                    if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mNumberButton.setBackgroundDrawable(
                                mCustomizationOptionsBundle.getButtonBackgroundDrawable());
                    } else {
                        holder.mNumberButton.setBackground(
                                mCustomizationOptionsBundle.getButtonBackgroundDrawable());
                    }
                }
                holder.mNumberButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mCustomizationOptionsBundle.getTextSize());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        mCustomizationOptionsBundle.getButtonSize(),
                        mCustomizationOptionsBundle.getButtonSize());
                holder.mNumberButton.setLayoutParams(params);
            }
        }
    }

    private void configureDeleteButtonHolder(DeleteViewHolder holder) {
        if (holder != null) {
            if (mCustomizationOptionsBundle.isShowDeleteButton() && mPinLength > 0) {
                holder.txvDelete.setTextColor(greenLightColor);
                /*holder.mButtonImage.setVisibility(View.VISIBLE);
                if (mCustomizationOptionsBundle.getDeleteButtonDrawable() != null) {
                    holder.mButtonImage.setImageDrawable(mCustomizationOptionsBundle.getDeleteButtonDrawable());
                }
                holder.mButtonImage.setColorFilter(mCustomizationOptionsBundle.getTextColor(),
                        PorterDuff.Mode.SRC_ATOP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        mCustomizationOptionsBundle.getDeleteButtonWidthSize(),
                        mCustomizationOptionsBundle.getDeleteButtonHeightSize());
                holder.mButtonImage.setLayoutParams(params);*/
            } else
                holder.txvDelete.setTextColor(whiteColor);
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_DELETE;
        }
        if (position == getItemCount() - 3) {
            return VIEW_TYPE_CANCEL;
        }
        return VIEW_TYPE_NUMBER;
    }

    public int getPinLength() {
        return mPinLength;
    }

    public void setPinLength(int pinLength) {
        this.mPinLength = pinLength;
    }

    public int[] getKeyValues() {
        return mKeyValues;
    }

    public void setKeyValues(int[] keyValues) {
        this.mKeyValues = getAdjustKeyValues(keyValues);
        notifyDataSetChanged();
    }

    private int[] getAdjustKeyValues(int[] keyValues) {
        int[] adjustedKeyValues = new int[keyValues.length + 1];
        for (int i = 0; i < keyValues.length; i++) {
            if (i < 9) {
                adjustedKeyValues[i] = keyValues[i];
            } else {
                adjustedKeyValues[i] = -1;
                adjustedKeyValues[i + 1] = keyValues[i];
            }
        }
        return adjustedKeyValues;
    }

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public OnNumberClickListener getOnItemClickListener() {
        return mOnNumberClickListener;
    }

    public void setOnItemClickListener(OnNumberClickListener onNumberClickListener) {
        this.mOnNumberClickListener = onNumberClickListener;
    }

    public OnDeleteClickListener getOnDeleteClickListener() {
        return mOnDeleteClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.mOnDeleteClickListener = onDeleteClickListener;
    }


    public CustomizationOptionsBundle getCustomizationOptions() {
        return mCustomizationOptionsBundle;
    }

    public void setCustomizationOptions(CustomizationOptionsBundle customizationOptionsBundle) {
        this.mCustomizationOptionsBundle = customizationOptionsBundle;
    }

    public interface OnNumberClickListener {
        void onNumberClicked(int keyValue);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked();

        void onDeleteLongClicked();
    }

    public interface onCancelClickListener {
        void onCancelClicked();
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder {
        Button mNumberButton;

        public NumberViewHolder(final View itemView, Typeface font) {
            super(itemView);
            mNumberButton = (Button) itemView.findViewById(R.id.button);

            if (font != null) {
                mNumberButton.setTypeface(font);
            }

            mNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNumberClickListener != null) {
                        mOnNumberClickListener.onNumberClicked((Integer) v.getTag());
                    }
                }
            });

            mNumberButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mNumberButton.startAnimation(scale());
                    }

                    return false;
                }
            });
        }
    }

    public class DeleteViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mDeleteButton;
        TextView txvDelete;

        public DeleteViewHolder(final View itemView) {
            super(itemView);
            mDeleteButton = (LinearLayout) itemView.findViewById(R.id.button);
//            mButtonImage = (ImageView) itemView.findViewById(R.id.buttonImage);
            txvDelete = itemView.findViewById(R.id.txvDelete);

            if (mCustomizationOptionsBundle.isShowDeleteButton() && mPinLength > 0) {
                txvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnDeleteClickListener != null) {
                            mOnDeleteClickListener.onDeleteClicked();
                        }
                    }
                });
            }
        }
    }

    public class CancelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mCancelButton;
        TextView txvCancel;

        public CancelViewHolder(final View itemView) {
            super(itemView);
            mCancelButton = (LinearLayout) itemView.findViewById(R.id.button);
            if (!isCancelable)
                mCancelButton.setVisibility(View.GONE);
            txvCancel = itemView.findViewById(R.id.txvDelete);
            txvCancel.setText(context.getString(R.string.msg_cancel));
            txvCancel.setTextColor(greenLightColor);
            txvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity) context).finish();
                }
            });
        }
    }

    private Animation scale() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(.75F, 1f, .75F, 1f,
                Animation.RELATIVE_TO_SELF, .5F, Animation.RELATIVE_TO_SELF, .5F);
        scaleAnimation.setDuration(BUTTON_ANIMATION_DURATION);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }
}

package at.fhooe.mc.android.mare.document;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import at.fhooe.mc.android.mare.R;

public class FontAdapter extends ArrayAdapter<String> {

    public FontAdapter(@NonNull Context context) {
        super(context, -1);
    }

    @NonNull
    @Override
    public View getView(int _position, @Nullable View _convertView, @NonNull ViewGroup parent) {
        if (_convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _convertView = inflater.inflate(R.layout.font_spinner_item, null);
        }

        String fontCode = getItem(_position);

        TextView tv = _convertView.findViewById(R.id.font_spinner_item_label);
        tv.setText(DocHeader.fontFamilyMap().get(fontCode));

        ImageView iv = _convertView.findViewById(R.id.font_spinner_item_image);
        iv.setVisibility(View.GONE);

        return _convertView;
    }

    @Override
    public View getDropDownView(int _position, @Nullable View _convertView, @NonNull ViewGroup parent) {
        if (_convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _convertView = inflater.inflate(R.layout.font_spinner_item, null);
        }

        View bg = _convertView.findViewById(R.id.font_spinner_item_background);
        bg.setBackgroundColor(Color.WHITE);

        String fontCode = getItem(_position);

        TextView tv = _convertView.findViewById(R.id.font_spinner_item_label);
        tv.setText(DocHeader.fontFamilyMap().get(fontCode));

        ImageView iv = _convertView.findViewById(R.id.font_spinner_item_image);
        iv.setImageBitmap(getBitmapFromAsset(getContext(), fontCode + ".png"));

        return _convertView;

    }


    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }
}

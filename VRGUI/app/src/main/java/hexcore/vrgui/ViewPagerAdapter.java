package hexcore.vrgui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Davina on 13/03/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;

    /**
     * Stores the different avatar images to be displayed from the 'drawable' folder
     */
    private Integer [] images = {R.drawable.male1,
                                    R.drawable.male2,
                                    R.drawable.male3,
                                    R.drawable.male4,
                                    R.drawable.male5,
                                    R.drawable.female1,
                                    R.drawable.female2,
                                    R.drawable.female3,
                                    R.drawable.female4,
                                    R.drawable.female5};

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    /**
     *
     * @return the number of images in the array
     */

    @Override
    public int getCount() {
        return images.length;
    }

    /**
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Initialises each image in the view page object ready for displaying
     * @param container
     * @param position in the array
     * @return view displaying the image is updated
     */

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0);
        view.setTag("myview" + position);
        return view;
    }

    /**
     * Removes an image from the view
     * @param container
     * @param position in the array
     * @param object to display
     */

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
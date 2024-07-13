package com.kprandro.newshow;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView rc;
    private List<NewsItemClass> newsItemClassList;
    private NewsAdapterClass newsAdapterClass;
    private SharedViewModel sharedViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rc = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rc.setLayoutManager(layoutManager);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rc);

        newsItemClassList = new ArrayList<>();
        newsItemClassList.add(new NewsItemClass(R.drawable.indiatv, "India TV", true, R.drawable.indiatvthumb, "Flood Crisis in Northern India", "India TV recently covered the devastating floods in northern India, focusing on the severe impact on states like Himachal Pradesh, Uttarakhand, and Punjab. The channel detailed how continuous heavy rains triggered landslides, washed away bridges, and displaced thousands of people. Emergency services and rescue operations were highlighted, with a focus on the efforts of the National Disaster Response Force (NDRF) and the Indian Army. India TV also discussed the long-term environmental and infrastructural damage caused by the floods, including the economic toll on agriculture and local businesses. The channel brought in climate experts to analyze the changing weather patterns in the region and how monsoon seasons have become more unpredictable due to climate change."));
        newsItemClassList.add(new NewsItemClass(R.drawable.aajtak, "Aaj Tak", true, R.drawable.aajtakthumb, "India's Economic Growth Forecast", "Aaj Tak recently focused on India's robust economic growth forecast for the upcoming fiscal year. The channel reported on how international financial institutions, including the World Bank and the International Monetary Fund (IMF), have praised India’s resilience in the face of global economic challenges, especially in the aftermath of the pandemic. Aaj Tak highlighted how key sectors like manufacturing, technology, and agriculture are contributing to this growth. The channel also explored the Indian government's economic policies, such as incentives for startups, infrastructure development, and foreign investments, which are driving the country's upward trajectory. Interviews with economists provided insight into how India is positioning itself as a global economic powerhouse."));
        newsItemClassList.add(new NewsItemClass(R.drawable.otv, "OTV", true, R.drawable.otvthumb, "Women's Reservation Bill in Parliament", "OTV recently covered the heated debates surrounding the Women's Reservation Bill, which proposes reserving 33% of seats for women in India’s Parliament and state assemblies. The channel aired discussions with political leaders, activists, and ordinary citizens who either support or oppose the bill. OTV focused on the regional perspectives, particularly how the bill would impact women's political representation in states like Odisha. The channel also highlighted protests and rallies organized by various women's rights groups across India, demanding immediate passage of the bill. Experts on OTV analyzed how the bill could reshape Indian politics by empowering women leaders and giving them a stronger voice in legislative processes."));
        newsItemClassList.add(new NewsItemClass(R.drawable.kanaka, "Kanaka News", true, R.drawable.kanakanewsthumb, "India’s Growing Renewable Energy Sector", "Kanak News featured an in-depth report on India's growing renewable energy sector, focusing on how the country is transitioning from fossil fuels to cleaner energy sources like solar and wind power. The channel discussed India's ambitious goals to reduce carbon emissions and increase the share of renewables in its energy mix. Kanak News highlighted various government initiatives, including the National Solar Mission and incentives for private companies investing in green energy. Interviews with energy experts and policymakers emphasized how renewable energy could help India achieve energy independence and contribute to global climate change efforts. The channel also explored the job opportunities emerging in this sector, especially for youth in rural areas."));
        newsItemClassList.add(new NewsItemClass(R.drawable.news, "News 18", true, R.drawable.newseightthumb, "India’s Diplomatic Ties with Southeast Asia", "News18 covered India's growing diplomatic and economic ties with Southeast Asian countries, emphasizing how India's \"Act East\" policy is strengthening relations with nations like Indonesia, Vietnam, and Singapore. The channel explored various trade agreements, joint military exercises, and cultural exchanges that have fostered closer relations between India and Southeast Asia. News18 also discussed India's strategic interests in the Indo-Pacific region, particularly in countering China's influence. Experts on the channel weighed in on how these partnerships are helping India expand its influence in international diplomacy and creating new economic opportunities, especially in sectors like defense, technology, and trade."));


        newsAdapterClass = new NewsAdapterClass(getContext(), newsItemClassList, sharedViewModel);
        rc.setAdapter(newsAdapterClass);

        return view;
    }
}
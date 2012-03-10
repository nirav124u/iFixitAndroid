package com.ifixit.android.ifixit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ifixit.android.sectionheaders.SectionHeadersAdapter;

import android.widget.TextView;

public class TopicListFragment extends Fragment
 implements TopicSelectedListener, OnItemClickListener {
   private static final String CURRENT_TOPIC = "CURRENT_TOPIC";
   private static final String PREVIOUS_TOPIC = "PREVIOUS_TOPIC";

   private TopicSelectedListener topicSelectedListener;
   private TopicNode mTopic;
   private String mPreviousTopic;
   private SectionHeadersAdapter mTopicAdapter;
   private Context mContext;
   private TextView mTopicText;
   private Button mBackButton;
   private ListView mListView;

   /**
    * Required for restoring fragments
    */
   public TopicListFragment() {}

   public TopicListFragment(TopicNode topic, String previousTopic) {
      mTopic = topic;
      mPreviousTopic = previousTopic;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      if (savedInstanceState != null) {
         mTopic = (TopicNode)savedInstanceState.getSerializable(
          CURRENT_TOPIC);
         mPreviousTopic = savedInstanceState.getString(PREVIOUS_TOPIC);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
	 Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.topic_list_fragment, container,
       false);

      mBackButton = (Button)view.findViewById(R.id.backButton);
      mBackButton.setOnClickListener(new OnClickListener() {
         public void onClick(View view) {
            getActivity().getSupportFragmentManager().popBackStack();
         }
      });
      mTopicText = (TextView)view.findViewById(R.id.currentTopic);
      mListView = (ListView)view.findViewById(R.id.topicList);
      mListView.setOnItemClickListener(this);

      setTopic(mTopic, mPreviousTopic);

      return view;
   }

   private void setupTopicAdapter() {
      mTopicAdapter = new SectionHeadersAdapter();
      ArrayList<TopicNode> generalInfo = new ArrayList<TopicNode>();
      ArrayList<TopicNode> nonLeaves = new ArrayList<TopicNode>();
      ArrayList<TopicNode> leaves = new ArrayList<TopicNode>();
      TopicListAdapter adapter;

      for (TopicNode topic : mTopic.getChildren()) {
         if (topic.isLeaf()) {
            leaves.add(topic);
         } else {
            nonLeaves.add(topic);
         }
      }

      // TODO add these to strings.xml

      if (!mTopic.isRoot()) {
         generalInfo.add(new TopicNode("General Information"));
         adapter = new TopicListAdapter(mContext, mTopic.getName(),
          generalInfo);
         adapter.setTopicSelectedListener(this);
         mTopicAdapter.addSection(adapter);
      }

      if (nonLeaves.size() > 0) {
         adapter = new TopicListAdapter(mContext, "Categories", nonLeaves);
         adapter.setTopicSelectedListener(this);
         mTopicAdapter.addSection(adapter);
      }

      if (leaves.size() > 0) {
         adapter = new TopicListAdapter(mContext, "Devices", leaves);
         adapter.setTopicSelectedListener(this);
         mTopicAdapter.addSection(adapter);
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);

      outState.putSerializable(CURRENT_TOPIC, mTopic);
      outState.putString(PREVIOUS_TOPIC, mPreviousTopic);
   }

   public void onItemClick(AdapterView<?> adapterView, View view,
    int position, long id) {
      mTopicAdapter.onItemClick(null, view, position, id);
   }

   public void onTopicSelected(TopicNode topic) {
      topicSelectedListener.onTopicSelected(topic);
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);

      try {
         topicSelectedListener = (TopicSelectedListener)activity;
         mContext = (Context)activity;
      } catch (ClassCastException e) {
         throw new ClassCastException(activity.toString() +
          " must implement TopicSelectedListener");
      }
   }

   private void setTopic(TopicNode topic, String previousTopic) {
      mTopic = topic;
      mPreviousTopic = previousTopic;
      setupTopicAdapter();
      mListView.setAdapter(mTopicAdapter);

      if (mPreviousTopic == null) {
         mBackButton.setVisibility(View.GONE);
      } else if (mPreviousTopic.equals(TopicNode.ROOT_NAME)) {
         // TODO: Add to strings.xml
         mBackButton.setText("Categories");
      } else {
         mBackButton.setText(mPreviousTopic);
      }

      mTopicText.setText(mTopic.getName());
   }
}
package com.ck.clump.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 5/2/17.
 */

public class SearchPlacesResponse {
    @SerializedName("results")
    @Expose
    public List<Result> results;
    @SerializedName("next_page_token")
    @Expose
    public String nextPageToken;

    public static class Result {
        @SerializedName("formatted_address")
        @Expose
        public String formattedAddress;
        @SerializedName("geometry")
        @Expose
        public Geometry geometry;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("icon")
        @Expose
        public String icon;

        //For load more item
        public int type;

        public Result(int type) {
            this.type = type;
        }

        public class Geometry {
            @SerializedName("location")
            @Expose
            public Location location;
            @SerializedName("viewport")
            @Expose
            public ViewPort viewport;

            public class ViewPort {
                @SerializedName("northeast")
                @Expose
                public Location northeast;

                @SerializedName("southwest")
                @Expose
                public Location southwest;
            }

            public class Location {
                @SerializedName("lat")
                @Expose
                public double lat;

                @SerializedName("lng")
                @Expose
                public double lng;
            }
        }
    }
}

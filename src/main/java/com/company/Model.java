package com.company;

class Model {
    static class ExtraTest {
        private int rank;

        ExtraTest(){}

        ExtraTest(int rank) {
            this.rank = rank;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }

    static class GameUser {
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    static class RequestParams {
        String userId;

        RequestParams(){}
        RequestParams(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}

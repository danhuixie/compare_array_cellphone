package com.voiceai.vprcjavasdk;

public class VoiceCloudRegisterInfo {


    /**
     * feature : {"num_utt":3,"avg_signal_energy":2.2857842,"avg_noise_energy":0.024675263,"avg_snr":19.724983,"gender":6.3154387E-4,"year":0,"month":0,"day":0,"hour":0,"status":0,"val1":0,"val2":0,"val3":0,"val4":0,"val5":0,"total_voice_length":4.97}
     * msg :
     * profile : {"analyze":{"extract":1480.87414550781,"function":1481.23278808594},"server":{"function":1497}}
     * result : true
     */

    private FeatureBean feature;
    private String msg;
    private ProfileBean profile;
    private boolean result;

    public FeatureBean getFeature() {
        return feature;
    }

    public void setFeature(FeatureBean feature) {
        this.feature = feature;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ProfileBean getProfile() {
        return profile;
    }

    public void setProfile(ProfileBean profile) {
        this.profile = profile;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public static class FeatureBean {
        /**
         * num_utt : 3
         * avg_signal_energy : 2.2857842
         * avg_noise_energy : 0.024675263
         * avg_snr : 19.724983
         * gender : 6.3154387E-4
         * year : 0
         * month : 0
         * day : 0
         * hour : 0
         * status : 0
         * val1 : 0
         * val2 : 0
         * val3 : 0
         * val4 : 0
         * val5 : 0
         * total_voice_length : 4.97
         */

        private int num_utt;
        private double avg_signal_energy;
        private double avg_noise_energy;
        private double avg_snr;
        private double gender;
        private int year;
        private int month;
        private int day;
        private int hour;
        private int status;
        private int val1;
        private int val2;
        private int val3;
        private int val4;
        private int val5;
        private double total_voice_length;

        public int getNum_utt() {
            return num_utt;
        }

        public void setNum_utt(int num_utt) {
            this.num_utt = num_utt;
        }

        public double getAvg_signal_energy() {
            return avg_signal_energy;
        }

        public void setAvg_signal_energy(double avg_signal_energy) {
            this.avg_signal_energy = avg_signal_energy;
        }

        public double getAvg_noise_energy() {
            return avg_noise_energy;
        }

        public void setAvg_noise_energy(double avg_noise_energy) {
            this.avg_noise_energy = avg_noise_energy;
        }

        public double getAvg_snr() {
            return avg_snr;
        }

        public void setAvg_snr(double avg_snr) {
            this.avg_snr = avg_snr;
        }

        public double getGender() {
            return gender;
        }

        public void setGender(double gender) {
            this.gender = gender;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getVal1() {
            return val1;
        }

        public void setVal1(int val1) {
            this.val1 = val1;
        }

        public int getVal2() {
            return val2;
        }

        public void setVal2(int val2) {
            this.val2 = val2;
        }

        public int getVal3() {
            return val3;
        }

        public void setVal3(int val3) {
            this.val3 = val3;
        }

        public int getVal4() {
            return val4;
        }

        public void setVal4(int val4) {
            this.val4 = val4;
        }

        public int getVal5() {
            return val5;
        }

        public void setVal5(int val5) {
            this.val5 = val5;
        }

        public double getTotal_voice_length() {
            return total_voice_length;
        }

        public void setTotal_voice_length(double total_voice_length) {
            this.total_voice_length = total_voice_length;
        }
    }

    public static class ProfileBean {
        /**
         * analyze : {"extract":1480.87414550781,"function":1481.23278808594}
         * server : {"function":1497}
         */

        private AnalyzeBean analyze;
        private ServerBean server;

        public AnalyzeBean getAnalyze() {
            return analyze;
        }

        public void setAnalyze(AnalyzeBean analyze) {
            this.analyze = analyze;
        }

        public ServerBean getServer() {
            return server;
        }

        public void setServer(ServerBean server) {
            this.server = server;
        }

        public static class AnalyzeBean {
            /**
             * extract : 1480.87414550781
             * function : 1481.23278808594
             */

            private double extract;
            private double function;

            public double getExtract() {
                return extract;
            }

            public void setExtract(double extract) {
                this.extract = extract;
            }

            public double getFunction() {
                return function;
            }

            public void setFunction(double function) {
                this.function = function;
            }
        }

        public static class ServerBean {
            /**
             * function : 1497
             */

            private int function;

            public int getFunction() {
                return function;
            }

            public void setFunction(int function) {
                this.function = function;
            }
        }
    }
}

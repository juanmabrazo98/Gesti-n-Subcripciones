package us.dit.fkbroker.service.services.fhir;

import java.util.List;

public class SubscriptionTopicDetails {
    private String name;
    private String url;
    private String id;
    private List<FilterDetail> filters;

    public SubscriptionTopicDetails(String name, String id, String url, List<FilterDetail> filters) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.filters = filters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<FilterDetail> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDetail> filters) {
        this.filters = filters;
    }

    public static class FilterDetail {
        private String description;
        private String filterParameter;
        private List<String> comparators;
        private List<String> modifiers;

        public FilterDetail(String description, String filterParameter, List<String> comparators, List<String> modifiers) {
            this.description = description;
            this.filterParameter = filterParameter;
            this.comparators = comparators;
            this.modifiers = modifiers;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFilterParameter() {
            return filterParameter;
        }

        public void setFilterParameter(String filterParameter) {
            this.filterParameter = filterParameter;
        }

        public List<String> getComparators() {
            return comparators;
        }

        public void setComparators(List<String> comparators) {
            this.comparators = comparators;
        }

        public List<String> getModifiers() {
            return modifiers;
        }

        public void setModifiers(List<String> modifiers) {
            this.modifiers = modifiers;
        }
    }
}
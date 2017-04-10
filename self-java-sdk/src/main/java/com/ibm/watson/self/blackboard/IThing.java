package com.ibm.watson.self.blackboard;

import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class IThing {

	public enum ThingEventType {
		TE_NONE(0),
		TE_ADDED(1),
		TE_REMOVED(2),
		TE_STATE(4),
		TE_IMPORTANCE(8);
		
		
		private int id;
		
		ThingEventType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
    public enum ThingCategory
    {
	    TT_INVALID(-1),
	    TT_PERCEPTION(0),
	    TT_AGENCY(1),
	    TT_MODEL(2);
	    
	    private int id;
	    
	    ThingCategory(int id) {
	    	this.id = id;
	    }
	    
	    public int getId() {
	    	return id;
	    }
	    
	    public void setId(int id) {
	    	this.id = id;
	    }
    }
	
    // Required
	private String type;
	private ThingCategory category;
	private String guid;
	private double importance;
	private String state;
	private long createTime;
	private double lifeSpan;
	
	// Optional
	private String dataType;
	private JsonObject data;
	private String parentId;
	private JsonObject body = null;
	private String origin;
	
	
	public IThing() {
		UUID uuid 	= UUID.randomUUID();
		
        setType(BlackBoardConstants.ITHING);
        setCategory(ThingCategory.TT_PERCEPTION);		
		setGuid(uuid.toString());
        setImportance(1.0f);
        setState(BlackBoardConstants.ADDED);
        setCreateTime((new Date().getTime()) / 1000);
        setLifeSpan(3600.0);
    }
	
	public JsonObject serialize() {
		JsonObject wrapperObject = new JsonObject();
		if(body != null) {
			for(Entry<String, JsonElement> entry : body.entrySet()) {
				String key = entry.getKey();
				if(entry.getValue().isJsonObject()) {
					JsonObject value = entry.getValue().getAsJsonObject();
					wrapperObject.add(key, value);
				}
				else {
					String value = entry.getValue().getAsString();
					wrapperObject.addProperty(key, value);
				}
			}
		}
		
		if(data != null) {
			for(Entry<String, JsonElement> entry : data.entrySet()) {
				String key = entry.getKey();
				if(entry.getValue().isJsonObject()) {
					JsonObject value = entry.getValue().getAsJsonObject();
					wrapperObject.add(key, value);
				}
				else {
					String value = entry.getValue().getAsString();
					wrapperObject.addProperty(key, value);
				}
			}
		}
		wrapperObject.addProperty(BlackBoardConstants.TYPE_, type);
		wrapperObject.addProperty(BlackBoardConstants.CATEGORY, category.getId());
		wrapperObject.addProperty(BlackBoardConstants.GUID, guid);
		wrapperObject.addProperty(BlackBoardConstants.M_IMPORTANCE, importance);
		wrapperObject.addProperty(BlackBoardConstants.M_STATE, state);
		wrapperObject.addProperty(BlackBoardConstants.LIFE_SPAN, lifeSpan);
		
		if(dataType != null && !dataType.isEmpty()) {
			wrapperObject.addProperty(BlackBoardConstants.DATA_TYPE, dataType);
			wrapperObject.add(BlackBoardConstants.DATA, data);
		}
		
		return wrapperObject;
	}
	
	public void deserialize(JsonObject wrapperObject) {
		this.body = wrapperObject;
		this.type = wrapperObject.get(BlackBoardConstants.TYPE_).getAsString();
		ThingCategory thingCategory = ThingCategory.TT_INVALID;
		thingCategory.setId(wrapperObject.get(BlackBoardConstants.CATEGORY).getAsInt());
		this.category = thingCategory;
		this.guid = wrapperObject.get(BlackBoardConstants.GUID).getAsString();
		this.state = wrapperObject.get(BlackBoardConstants.M_STATE).getAsString();
		
		if(wrapperObject.has(BlackBoardConstants.M_IMPORTANCE)) {
			this.importance = wrapperObject.get(BlackBoardConstants.M_IMPORTANCE).getAsDouble();
		}
		if(wrapperObject.has(BlackBoardConstants.CREATE_TIME)) {
			this.createTime = (long) wrapperObject.get(BlackBoardConstants.CREATE_TIME).getAsDouble();
		}
		if(wrapperObject.has(BlackBoardConstants.LIFE_SPAN)) {
			this.lifeSpan = wrapperObject.get(BlackBoardConstants.LIFE_SPAN).getAsDouble();
		}
		if(wrapperObject.has(BlackBoardConstants.DATA_TYPE)) {
			this.dataType = wrapperObject.get(BlackBoardConstants.DATA_TYPE).getAsString();
		}
		if(wrapperObject.has(BlackBoardConstants.DATA)) {
			this.data = wrapperObject.get(BlackBoardConstants.DATA).getAsJsonObject();
		}
	}
	
	public String toString() {
		return body.toString();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ThingCategory getCategory() {
		return category;
	}

	public void setCategory(ThingCategory category) {
		this.category = category;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public double getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(double lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public JsonObject getData() {
		return data;
	}

	public void setData(JsonObject data) {
		this.data = data;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public JsonObject getBody() {
		return body;
	}
	
	public void setBody(JsonObject body) {
		this.body = body;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
}

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
	
	
	public IThing() {
		UUID uuid 	= UUID.randomUUID();
		
        setType("IThing");
        setCategory(ThingCategory.TT_PERCEPTION);		
		setGuid(uuid.toString());
        setImportance(1.0f);
        setState("ADDED");
        setCreateTime((new Date().getTime()) / 1000);
        setLifeSpan(3600.0);
    }
	
	public String serialize() {
		JsonObject wrapperObject = new JsonObject();
		if(body != null) {
			for(Entry<String, JsonElement> entry : body.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().getAsString();
				wrapperObject.addProperty(key, value);
			}
		}
		wrapperObject.addProperty("Type_", type);
		wrapperObject.addProperty("m_eCategory", category.getId());
		wrapperObject.addProperty("m_GUID", guid);
		wrapperObject.addProperty("m_fImportance", importance);
		wrapperObject.addProperty("m_State", state);
		wrapperObject.addProperty("m_fLifeSpan", lifeSpan);
		
		if(!dataType.isEmpty()) {
			wrapperObject.addProperty("m_DataType", dataType);
			wrapperObject.add("m_Data", data);
		}
		
		return wrapperObject.toString();
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
}

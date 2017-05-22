package org.homeautomation.androidtv.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.androidtv.api.constants.AndroidTVConstants;
import org.homeautomation.androidtv.plugin.constants.DeviceTypeConstants;
import org.homeautomation.androidtv.plugin.impl.DeviceTypeManager;
import org.wso2.carbon.analytics.datasource.commons.Record;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.util.Utils;
import org.wso2.carbon.device.mgt.common.DeviceManager;
import org.wso2.carbon.device.mgt.common.authorization.DeviceAccessAuthorizationService;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfigurationManagementService;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagementService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides utility functions used by REST-API.
 */
public class APIUtil {

	private static Log log = LogFactory.getLog(APIUtil.class);

	public static String getAuthenticatedUser() {
		PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		String username = threadLocalCarbonContext.getUsername();
		String tenantDomain = threadLocalCarbonContext.getTenantDomain();
		if (username.endsWith(tenantDomain)) {
			return username.substring(0, username.lastIndexOf("@"));
		}
		return username;
	}

	public static String getAuthenticatedUserTenantDomain() {
		PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		return threadLocalCarbonContext.getTenantDomain();
	}

	public static DeviceManagementProviderService getDeviceManagementService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		DeviceManagementProviderService deviceManagementProviderService =
				(DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
		if (deviceManagementProviderService == null) {
			String msg = "Device Management service has not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return deviceManagementProviderService;
	}

	/**
	 * Creates the SensorDatas from records.
	 *
	 * @param records the records
	 * @return the Map of SensorRecord <id, SensorRecord>
	 */
	public static Map<String, SensorRecord> createSensorData(List<Record> records) {
		Map<String, SensorRecord> sensorDatas = new HashMap<>();
		for (Record record : records) {
			SensorRecord sensorData = createSensorData(record);
			sensorDatas.put(sensorData.getId(), sensorData);
		}
		return sensorDatas;
	}

	/**
	 * Create a SensorRecord object out of a Record object
	 *
	 * @param record the record object
	 * @return SensorRecord object
	 */
	public static SensorRecord createSensorData(Record record) {
		SensorRecord recordBean = new SensorRecord();
		recordBean.setId(record.getId());
		recordBean.setValues(record.getValues());
		return recordBean;
	}

	public static APIManagementProviderService getAPIManagementProviderService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		APIManagementProviderService apiManagementProviderService =
				(APIManagementProviderService) ctx.getOSGiService(APIManagementProviderService.class, null);
		if (apiManagementProviderService == null) {
			String msg = "API management provider service has not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return apiManagementProviderService;
	}

	public static DeviceAccessAuthorizationService getDeviceAccessAuthorizationService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		DeviceAccessAuthorizationService deviceAccessAuthorizationService =
				(DeviceAccessAuthorizationService) ctx.getOSGiService(DeviceAccessAuthorizationService.class, null);
		if (deviceAccessAuthorizationService == null) {
			String msg = "Device Authorization service has not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return deviceAccessAuthorizationService;
	}

	public static PlatformConfigurationManagementService getTenantConfigurationManagementService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		PlatformConfigurationManagementService tenantConfigurationManagementService =
				(PlatformConfigurationManagementService) ctx.getOSGiService(PlatformConfigurationManagementService.class, null);
		if (tenantConfigurationManagementService == null) {
			String msg = "Tenant cdmf.unit.device.type.android_tv.platform.configuration Management service not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return tenantConfigurationManagementService;
	}

	public static String getMqttEndpoint() throws ConfigurationManagementException {
		String iotServerIP = Utils.replaceSystemProperty(AndroidTVConstants.DEFAULT_ENDPOINT);
		iotServerIP = iotServerIP.replace(AndroidTVConstants.LOCALHOST, getServerUrl());;
		PlatformConfiguration configuration = APIUtil.getTenantConfigurationManagementService().getConfiguration(
				AndroidTVConstants.CONFIG_TYPE);
		if (configuration != null && configuration.getConfiguration() != null && configuration
				.getConfiguration().size() > 0) {
			List<ConfigurationEntry> configurations = configuration.getConfiguration();
			for (ConfigurationEntry configurationEntry : configurations) {
				switch (configurationEntry.getName()) {
					case "ANDROID_SENSE_MQTT_EP":
						iotServerIP = (String)configurationEntry.getValue();
						break;
				}
			}
		}
		return iotServerIP;
	}

	public static String getServerUrl() {
		try {
			return org.apache.axis2.util.Utils.getIpAddress();
		} catch (SocketException e) {
			log.warn("Failed retrieving the hostname, therefore set to localhost", e);
            return "localhost";
		}
	}

    public static DeviceTypeManager getDeviceTypeManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        List<Object> deviceManagementServices = ctx.getOSGiServices(DeviceManagementService.class, null);
        DeviceTypeManager deviceTypeManager = null;
        for (Object service : deviceManagementServices) {
            DeviceManagementService dmService = (DeviceManagementService) service;
            if (DeviceTypeConstants.DEVICE_TYPE.equals(dmService.getType())
                && DeviceTypeManager.class.isInstance(dmService.getDeviceManager())){
                deviceTypeManager = (DeviceTypeManager) dmService.getDeviceManager();
            }
        }

        if (deviceTypeManager == null) {
            String msg = "Device Type Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }else{
            return deviceTypeManager;
        }
    }
}

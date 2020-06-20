/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.service.map.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "map-cache")
public class MapCacheConfig {

    private boolean usePointTileCache;

    private boolean useHeatmapTileCache;

    private boolean useShapeTileCache;

    private int cacheIgnoreZoomLevel;

    private int cacheStaleSeconds;

    private int cacheExpireSeconds;

    public boolean getUsePointTileCache() {
        return usePointTileCache;
    }

    public void setUsePointTileCache(boolean usePointTileCache) {
        this.usePointTileCache = usePointTileCache;
    }

    public boolean getUseHeatmapTileCache() {
        return useHeatmapTileCache;
    }

    public void setUseHeatmapTileCache(boolean useHeatmapTileCache) {
        this.useHeatmapTileCache = useHeatmapTileCache;
    }

    public boolean getUseShapeTileCache() {
        return useShapeTileCache;
    }

    public void setUseShapeTileCache(boolean useShapeTileCache) {
        this.useShapeTileCache = useShapeTileCache;
    }

    public int getCacheIgnoreZoomLevel() {
        return cacheIgnoreZoomLevel;
    }

    public void setCacheIgnoreZoomLevel(int cacheIgnoreZoomLevel) {
        this.cacheIgnoreZoomLevel = cacheIgnoreZoomLevel;
    }

    public int getCacheStaleSeconds() {
        return cacheStaleSeconds;
    }

    public void setCacheStaleSeconds(int cacheStaleSeconds) {
        this.cacheStaleSeconds = cacheStaleSeconds;
    }

    public int getCacheExpireSeconds() {
        return cacheExpireSeconds;
    }

    public void setCacheExpireSeconds(int cacheExpireSeconds) {
        this.cacheExpireSeconds = cacheExpireSeconds;
    }

}

/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
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

package at.alladin.nntool.client.helper;

import java.io.InputStream;
import java.util.Properties;

public final class RevisionHelper {
    public static final String DESCRIBE;
    public static final String REVISION;
    public static final String GITID;
    public static final String BRANCH;
    public static final long BUILDTIME;
    public static final boolean DIRTY;

    private RevisionHelper() {
    }

    static {
        String describe = null;
        String revision = null;
        String gitId = null;
        String branch = null;
        Long buildTime = null;
        boolean dirty = false;
        final InputStream svnIS = RevisionHelper.class.getClassLoader().getResourceAsStream("revision.properties");
        final Properties properties = new Properties();
        if (svnIS != null) {
            try {
                properties.load(svnIS);
                describe = properties.getProperty("git.describe");
                gitId = properties.getProperty("git.id");
                branch = properties.getProperty("git.branch");
                final String dirtyString = properties.getProperty("git.dirty");
                dirty = dirtyString != null && dirtyString.equals("true");
                revision = properties.getProperty("git.revision");
                if (dirty) {
                    revision = revision + "M";
                    gitId = gitId + "M";
                }
                buildTime = Long.parseLong(properties.getProperty("build.time"));
            } catch (final Exception e) { // there isn't much we can do here about it..
            }
        }
        DESCRIBE = describe == null ? "?" : describe;
        REVISION = revision == null ? "?" : revision;
        GITID = gitId == null ? "?" : gitId;
        BRANCH = branch == null ? "?" : branch;
        DIRTY = dirty;
        BUILDTIME = buildTime == null ? 0 : buildTime;
    }

    public static String getVerboseRevision() {
        return String.format("%s_%s", BRANCH, DESCRIBE);
    }

    public static long getVeboseTimestamp() {
        return BUILDTIME;
    }

    public static String getServerVersion() {
        return GITID;
    }
}

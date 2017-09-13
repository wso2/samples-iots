'''
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
'''

import os
try:
    import pip
except ImportError:
    os.system('python get-pip.py')

def import_or_install(package):
    try:
        __import__(package)
        print('already there')
    except ImportError:
        pip.main(['install', package])

def installMissingPackages():
    os.system('pip install -Iv paho-mqtt==1.2.3')